package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import query.ParallelQueryExecution;
import query.SQLQuery;
import schema.TableStatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParallelTempTableQueryExecutor extends TempTableQueryExecutor {
    private ConnectionPool connectionPool;
    private boolean enableEarlyTermination = true;
    private boolean dropTables = true;
    private boolean createIndexes = false;
    private boolean depthOpt = false;
    private String schemaFile = null;

    private long[] stageRuntimes = new long[] {-1,-1,-1,-1};

    public ParallelTempTableQueryExecutor(ConnectionPool connectionPool, boolean useStatistics,
                                          boolean enableEarlyTermination, boolean dropTables,
                                          boolean createIndexes, boolean depthOpt) throws SQLException {
        /**
         * We need the connection pool because connections are processed on a
         * single core in postgres: https://stackoverflow.com/questions/32629988/query-parallelization-for-single-connection-in-postgres
         */
        // TODO refactor constructors
        super(connectionPool, useStatistics);
        this.connectionPool = connectionPool;
        this.enableEarlyTermination = enableEarlyTermination;
        this.dropTables = dropTables;
        this.createIndexes = createIndexes;
        this.depthOpt = depthOpt;
    }

    public ParallelTempTableQueryExecutor(ConnectionPool connectionPool, boolean useStatistics,
                                          boolean enableEarlyTermination, boolean dropTables,
                                          boolean createIndexes, boolean depthOpt, String schemaFile) throws SQLException {
        this(connectionPool, useStatistics, enableEarlyTermination, dropTables, createIndexes, depthOpt);
        this.schemaFile = schemaFile;
    }

    public ParallelTempTableQueryExecutor(ConnectionPool connectionPool) throws SQLException {
        /**
         * We need the connection pool because connections are processed in a
         * single core in postgres: https://stackoverflow.com/questions/32629988/query-parallelization-for-single-connection-in-postgres
         */
        super(connectionPool);
        this.connectionPool = connectionPool;
    }

    @Override
    public ResultSet execute(String queryStr, boolean booleanQuery) throws SQLException, QueryConversionException, TableNotFoundException {
        StatisticsResultSet statisticsResultSet = executeWithStatistics(queryStr, booleanQuery);

        return statisticsResultSet.getResultSet();
    }

    public StatisticsResultSet executeWithStatistics(String queryStr, boolean booleanQuery) throws SQLException, QueryConversionException, TableNotFoundException {
        return executeWithStatistics(queryStr, booleanQuery, false);
    }

    public StatisticsResultSet executeWithStatistics(String queryStr, boolean booleanQuery, boolean analyze) throws SQLException, QueryConversionException, TableNotFoundException {

        long preprocessingStartTime = System.currentTimeMillis();

        sqlQuery = new SQLQuery(queryStr, schema);
        sqlQuery.setDecompositionOptions(decompositionOptions);
        sqlQuery.setCreateIndexes(createIndexes);
        if (schemaFile != null) {
            sqlQuery.setSchemaFile(schemaFile);
        }

        if (useStatistics) {
            Map<String, TableStatistics> statisticsMap = new HashMap<>();
            for (String tableName : sqlQuery.getRealTables()) {
                statisticsMap.put(tableName, extractTableStatistics(tableName));
            }
            sqlQuery.setStatistics(statisticsMap);
            sqlQuery.setConnection(connection);
        }
        ParallelQueryExecution queryExecution = sqlQuery.toParallelExecution(booleanQuery);

        System.out.println(queryExecution.getSqlStatements());

        this.totalPreprocessingTime = System.currentTimeMillis() - preprocessingStartTime;

        this.generatedFunction = "";

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();

        long totalStartTime = System.currentTimeMillis();

        List<ExecutionStatistics> statisticsList = new LinkedList<>();

        AtomicBoolean emptyResult = new AtomicBoolean(false);

        try {
            int layerCount = 0;
            int stageIndex = 0;
            for (List<List<String>> stage : queryExecution.getSqlStatements()) {
                long stageStartTime = System.currentTimeMillis();

                for (List<String> layer : stage) {
                    List<String> analyzeJSONs = new LinkedList<>();
                    List<String> analyzeJSONQueryStrings = new LinkedList<>();
                    long startTime = System.currentTimeMillis();

                    if (emptyResult.get()) {
                        break;
                    }
                    Set<PreparedStatement> runningQueries = ConcurrentHashMap.newKeySet();
                    try {
                        layer.parallelStream().forEach(query -> {
                            this.generatedFunction += query + "\n";
                            System.out.println("-- executing query: \n" + query);
                            try {
                                Connection conn = connectionPool.getConnection();

                                if (analyze) {
                                    // If a view is created, query execution is deferred and no query plan is made
                                    if (!query.startsWith("CREATE VIEW")) {
                                        PreparedStatement ps = conn.prepareStatement("EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) " + query);
                                        ResultSet rs = ps.executeQuery();

                                        String analyzeJSONString = "";
                                        while (rs.next()) {
                                            analyzeJSONString += rs.getString(1);
                                        }
                                        analyzeJSONs.add(analyzeJSONString);
                                        analyzeJSONQueryStrings.add(query);
                                    } else {
                                        PreparedStatement ps = conn.prepareStatement(query);
                                        ps.execute();
                                    }
                                } else {
                                    PreparedStatement ps = conn.prepareStatement(query);
                                    runningQueries.add(ps);
                                    ps.execute();
                                    runningQueries.remove(ps);

                                    if (enableEarlyTermination && query.startsWith("CREATE UNLOGGED TABLE")) {
                                        Matcher matcher = Pattern.compile("CREATE\\s+UNLOGGED\\s+TABLE\\s+(.*)\\s+").matcher(query);
                                        matcher.find();
                                        String tableName = matcher.group(1);
                                        System.out.println("tableName: " + tableName);
                                        PreparedStatement getCountStmt = connection.prepareStatement(String.format("SELECT count(*) as n_rows FROM %s;", tableName));

                                        ResultSet rsCount = getCountStmt.executeQuery();
                                        rsCount.next();
                                        int rowCount = rsCount.getInt("n_rows");
                                        rsCount.close();
                                        getCountStmt.close();

                                        if (rowCount == 0) {
                                            emptyResult.set(true);
                                            System.out.println("result is empty!");
                                            throw new StopParallelStreamException();
                                        }
                                    }
                                }
                                connectionPool.returnConnection(conn);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        });
                    }
                    catch (StopParallelStreamException e) {
                        System.out.println("Stopped parallel execution - cancelling active queries");
                        for (PreparedStatement ps : runningQueries) {
                            System.out.println("Cancelling query");
                            //ps.cancel();
                        }
                    }

                    System.out.println("-- time elapsed: " + (System.currentTimeMillis() - startTime));
                    long timeDifference = System.currentTimeMillis() - startTime;

                    statisticsList.add(new AnalyzeExecutionStatistics("layer-" + layerCount,
                            layer, timeDifference, analyzeJSONs, analyzeJSONQueryStrings));
                    layerCount++;
                }

                long stageTime = System.currentTimeMillis() - stageStartTime;
                stageRuntimes[stageIndex] = stageTime;
                stageIndex++;
            }
        }
        catch (RuntimeException e) {
            // Remove temp views / tables TODO refactor to avoid duplicate code
            for (String dropStatement : queryExecution.getDropStatements().toList()) {
                connection.prepareStatement(dropStatement).execute();
            }

            // It's not possible to throw a checked exception in a lambda function
            // TODO write a custom SQLRuntimeException
            throw new SQLException(e);
        }

        long stage4StartTime = System.currentTimeMillis();

        PreparedStatement psSelect = connection.prepareStatement(
                String.format(
                        booleanQuery ? "SELECT * FROM %s LIMIT 1;" : "SELECT * FROM %s;",
                        queryExecution.getFinalSelectName()),
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        if (timeout != null) {
            psSelect.setQueryTimeout(timeout);
        }
        psSelect.closeOnCompletion();

        ResultSet rs;
        if (emptyResult.get()) {
            // TODO find a nicer solution?
            PreparedStatement noRowsStmt = connection.prepareStatement(String.format("SELECT 1 LIMIT 0;"),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = noRowsStmt.executeQuery();
        }
        else {
            rs = psSelect.executeQuery();
        }

        long stage4Time = System.currentTimeMillis() - stage4StartTime;
        stageRuntimes[3] = stage4Time;

        queryRunningTime = System.currentTimeMillis() - totalStartTime;

        System.out.println("total time elapsed: " + queryRunningTime);

        long dropStartTime = System.currentTimeMillis();

        if (dropTables) {
            // Remove temp views / tables
            for (String dropStatement : queryExecution.getDropStatements().toList()) {
                connection.prepareStatement(dropStatement).execute();
            }
        }

        dropTime = System.currentTimeMillis() - dropStartTime;

        return new StatisticsResultSet(rs, statisticsList);
    }

    public long[] getStageRuntimes() {
        return stageRuntimes;
    }
}
