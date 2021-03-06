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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParallelTempTableQueryExecutor extends TempTableQueryExecutor {
    private ConnectionPool connectionPool;

    public ParallelTempTableQueryExecutor(ConnectionPool connectionPool, boolean useStatistics) throws SQLException {
        /**
         * We need the connection pool because connections are processed on a
         * single core in postgres: https://stackoverflow.com/questions/32629988/query-parallelization-for-single-connection-in-postgres
         */
        // TODO refactor constructors
        super(connectionPool, useStatistics);
        this.connectionPool = connectionPool;
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
        sqlQuery = new SQLQuery(queryStr, schema);
        sqlQuery.setDecompositionOptions(decompositionOptions);

        if (useStatistics) {
            Map<String, TableStatistics> statisticsMap = new HashMap<>();
            for (String tableName : sqlQuery.getRealTables()) {
                statisticsMap.put(tableName, extractTableStatistics(tableName));
            }
            sqlQuery.setStatistics(statisticsMap);
        }
        ParallelQueryExecution queryExecution = sqlQuery.toParallelExecution(booleanQuery);

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();

        long totalStartTime = System.currentTimeMillis();

        List<ExecutionStatistics> statisticsList = new LinkedList<>();

        try {
            int layerCount = 0;
            for (List<String> layer : queryExecution.getSqlStatements()) {
                long startTime = System.currentTimeMillis();

                layer.parallelStream().forEach(query -> {
                    System.out.println("-- executing query: \n" + query);
                    System.out.println("query: " + query);
                    try {
                        Connection conn = connectionPool.getConnection();
                        PreparedStatement ps = conn.prepareStatement(query);
                        ps.execute();
                        connectionPool.returnConnection(conn);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("-- time elapsed: " + (System.currentTimeMillis() - startTime));
                long timeDifference = System.currentTimeMillis() - startTime;

                statisticsList.add(new ExecutionStatistics("layer-" + layerCount, layer, timeDifference));
                layerCount++;
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
        ResultSet rs = psSelect.executeQuery();

        queryRunningTime = System.currentTimeMillis() - totalStartTime;

        System.out.println("total time elapsed: " + queryRunningTime);

        // Remove temp views / tables
        for (String dropStatement : queryExecution.getDropStatements().toList()) {
            connection.prepareStatement(dropStatement).execute();
        }

        return new StatisticsResultSet(rs, statisticsList);
    }
}
