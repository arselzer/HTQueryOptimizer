package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import hypergraph.Hypergraph;
import query.JoinTreeNode;
import query.SQLQuery;
import schema.Column;
import schema.DBSchema;
import schema.Table;
import schema.TableStatistics;

import java.sql.*;
import java.util.*;

public class TempTableQueryExecutor implements QueryExecutor {
    protected Connection connection;
    protected DBSchema schema;
    protected DecompositionOptions decompositionOptions;
    protected SQLQuery sqlQuery;

    protected long queryRunningTime;
    protected long dropTime;
    protected long totalPreprocessingTime;
    protected Hypergraph hypergraph;
    protected JoinTreeNode joinTree;
    protected String generatedFunction;

    protected Integer timeout = null;
    protected boolean useStatistics = true;

    public TempTableQueryExecutor(ConnectionPool connectionPool, boolean useStatistics) throws SQLException {
        try {
            this.connection = connectionPool.getConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.decompositionOptions = new DecompositionOptions();
        this.useStatistics = useStatistics;

        extractSchema();
    }

    public TempTableQueryExecutor(ConnectionPool connectionPool) throws SQLException {
        try {
            this.connection = connectionPool.getConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.decompositionOptions = new DecompositionOptions();

        extractSchema();
    }

    public void setDecompositionOptions(DecompositionOptions decompositionOptions) {
        this.decompositionOptions = decompositionOptions;
    }

    @Override
    public void setTimeout(int n) {
        timeout = n;
    }

    @Override
    public ResultSet execute(PreparedStatement ps) throws SQLException, QueryConversionException, TableNotFoundException {
        String query = ps.toString(); // Should work with postgres (and mysql) drivers

        return execute(query);
    }

    @Override
    public ResultSet executeBoolean(PreparedStatement ps) throws SQLException, QueryConversionException, TableNotFoundException {
        String query = ps.toString();

        return execute(query, true);
    }

    @Override
    public ResultSet execute(String queryStr) throws SQLException, QueryConversionException, TableNotFoundException {
        return execute(queryStr, false);
    }

    public ResultSet execute(String queryStr, boolean booleanQuery) throws SQLException, QueryConversionException, TableNotFoundException {
        StatisticsResultSet statisticsResultSet = executeWithStatistics(queryStr, booleanQuery);

        return statisticsResultSet.getResultSet();
    }

    public StatisticsResultSet executeWithStatistics(String queryStr, boolean booleanQuery) throws SQLException, QueryConversionException, TableNotFoundException {
        sqlQuery = new SQLQuery(queryStr, schema);
        sqlQuery.setDecompositionOptions(decompositionOptions);

        if (useStatistics) {
            Map<String, TableStatistics> statisticsMap = new HashMap<>();
            for (String tableName : sqlQuery.getAliasTables()) {
                statisticsMap.put(tableName, extractTableStatistics(tableName));
            }
            sqlQuery.setStatistics(statisticsMap);
        }

        List<ExecutionStatistics> statisticsList = new LinkedList<>();

        String functionName = SQLQuery.generateFunctionName();
        String functionStr = sqlQuery.toFunction(functionName);

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();
        this.generatedFunction = functionStr;

        long startTime = System.currentTimeMillis();

        try (PreparedStatement psFunction = connection.prepareStatement(functionStr)) {
            psFunction.execute();

            PreparedStatement psSelect = connection.prepareStatement(
                    String.format(booleanQuery ? "SELECT * FROM %s() LIMIT 1;" : "SELECT * FROM %s();", functionName),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if (timeout != null) {
                psSelect.setQueryTimeout(timeout);
            }
            psSelect.closeOnCompletion();
            ResultSet rs = psSelect.executeQuery();

            queryRunningTime = System.currentTimeMillis() - startTime;

            statisticsList.add(new ExecutionStatistics("query", List.of(functionStr), queryRunningTime));

            System.out.println("total time elapsed: " + queryRunningTime);

            //PreparedStatement psSelectFromView = connection.prepareStatement(String.format("SELECT * FROM %s", finalTableName));
            //ResultSet rs = psSelectFromView.executeQuery();

            // TODO maybe keep the function over several calls for performance
            PreparedStatement psDelete = connection.prepareStatement(String.format("DROP FUNCTION %s;", functionName));
            psDelete.execute();
            psDelete.close();

            //enableMergeJoin();
            // TODO disabling merge join alters global db state ? - maybe isolate it if possible

            return new StatisticsResultSet(rs, statisticsList);
        }
    }

    @Override
    public ResultSet executeBoolean(String query) throws SQLException, QueryConversionException, TableNotFoundException {
        return execute(query, true);
    }

    private DBSchema getSchema() {
        return schema;
    }

    public long getQueryRunningTime() {
        return queryRunningTime;
    }

    public long getTotalPreprocessingTime() {
        return totalPreprocessingTime;
    }

    public long getDropTime() {
        return dropTime;
    }

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public JoinTreeNode getJoinTree() {
        return joinTree;
    }

    public SQLQuery getQuery() {
        return sqlQuery;
    }

    public String getGeneratedFunction() {
        return generatedFunction;
    }

    private void enableMergeJoin() throws SQLException {
        connection.prepareStatement("SET enable_mergejoin = 0;").execute();
    }

    private void disableMergeJoin() throws SQLException {
        connection.prepareStatement("SET enable_mergejoin = 1;").execute();
    }

    protected TableStatistics extractTableStatistics(String tableName) throws SQLException {
        PreparedStatement getCountStmt = connection.prepareStatement(String.format("SELECT count(*) as n_rows FROM %s;", tableName));

        ResultSet rsCount = getCountStmt.executeQuery();
        rsCount.next();
        int rowCount = rsCount.getInt("n_rows");
        rsCount.close();
        getCountStmt.close();

        HashMap<String, Map<String, Double>> mostFrequentValues = new HashMap<>();

        // most_common_vals needs to be converted to a string array since the jdbc driver cannot work with its type
        // See https://stackoverflow.com/questions/52189147/loading-a-non-materialised-array-from-postgres-in-java
        PreparedStatement getStatisticsStmt = connection.prepareStatement("SELECT tablename, attname, most_common_vals::text::text[] as most_common_vals, most_common_freqs" +
                        " FROM pg_stats WHERE tablename = ?;");
        getStatisticsStmt.setString(1, tableName);

        ResultSet rsStatistics = getStatisticsStmt.executeQuery();

        while (rsStatistics.next()) {
            HashMap<String, Double> mostFrequentValuesForColumn = new HashMap<>();

            String columnName = rsStatistics.getString("attname");
            if (rsStatistics.getArray("most_common_vals") == null) {
                // Ignore the c_comment column or any columns without histograms
                mostFrequentValues.put(columnName, mostFrequentValuesForColumn);
                continue;
            }
            String[] mostFrequentValsArray = (String[]) rsStatistics.getArray("most_common_vals").getArray();
            Float[] mostFrequentValsFrequenciesArray = (Float[]) rsStatistics.getArray("most_common_freqs").getArray();
            //System.out.println(columnName + " " + Arrays.toString(mostFrequentValsArray) + " " + Arrays.toString(mostFrequentValsFrequenciesArray));
            for (int i = 0; i < mostFrequentValsArray.length; i++) {
                mostFrequentValuesForColumn.put(mostFrequentValsArray[i], (double) mostFrequentValsFrequenciesArray[i]);
            }
            mostFrequentValues.put(columnName, mostFrequentValuesForColumn);
            //System.out.println(tableName + "." + columnName + ": " + mostFrequentValuesForColumn.keySet() + ", " + mostFrequentValuesForColumn.values());

            // TODO extract histogram
        }

        rsStatistics.close();
        getStatisticsStmt.close();

        return new TableStatistics(rowCount, mostFrequentValues);
    }

    private void extractSchema() throws SQLException {
        schema = new DBSchema();

        DatabaseMetaData metaData = connection.getMetaData();

        ResultSet rs = metaData.getTables(connection.getCatalog(), "public", "%", new String[]{"TABLE"});

        // https://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html#getColumns(java.lang.String,%20java.lang.String,%20java.lang.String,%20java.lang.String)
        List<Table> schemaTables = new LinkedList<>();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            if (!tableName.startsWith("sql_")) {
                // Ignore internal tables

                Table table = new Table();
                table.setName(tableName);

                ResultSet rs2 = metaData.getColumns(null, null, tableName, null);
                LinkedList<Column> tableColumns = new LinkedList<>();
                while (rs2.next()) {
                    String columnName = rs2.getString("COLUMN_NAME");
                    String columnPostgresType = rs2.getString("TYPE_NAME");
                    tableColumns.add(new Column(columnName, columnPostgresType));
                }
                table.setColumns(tableColumns);
                schemaTables.add(table);
            }
        }

        schema.setTables(schemaTables);
    }
}
