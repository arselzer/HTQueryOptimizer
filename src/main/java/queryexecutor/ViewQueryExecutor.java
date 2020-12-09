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

public class ViewQueryExecutor implements QueryExecutor {
    private Connection connection;
    private DBSchema schema;
    private DecompositionOptions decompositionOptions;
    private SQLQuery sqlQuery;

    private long queryRunningTime;
    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;
    private String generatedFunction;

    private Integer timeout = null;

    public ViewQueryExecutor(Connection connection) throws SQLException {
        this.connection = connection;
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
    public ResultSet execute(String queryStr) throws SQLException, QueryConversionException, TableNotFoundException {
        sqlQuery = new SQLQuery(queryStr, schema);
        sqlQuery.setDecompositionOptions(decompositionOptions);

        for (Table table: schema.getTables()) {
            TableStatistics statistics = extractTableStatistics(table.getName());
        }

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
                    String.format("SELECT * FROM %s();", functionName),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if (timeout != null) {
                psSelect.setQueryTimeout(timeout);
            }
            psSelect.closeOnCompletion();
            ResultSet rs = psSelect.executeQuery();

            queryRunningTime = System.currentTimeMillis() - startTime;

            //PreparedStatement psSelectFromView = connection.prepareStatement(String.format("SELECT * FROM %s", finalTableName));
            //ResultSet rs = psSelectFromView.executeQuery();

            // TODO maybe keep the function over several calls for performance
            PreparedStatement psDelete = connection.prepareStatement(String.format("DROP FUNCTION %s;", functionName));
            psDelete.execute();
            psDelete.close();

            //enableMergeJoin();
            // TODO disabling merge join alters global db state ? - maybe isolate it if possible

            return rs;
        }
    }

    private DBSchema getSchema() {
        return schema;
    }

    public long getQueryRunningTime() {
        return queryRunningTime;
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

    private TableStatistics extractTableStatistics(String tableName) throws SQLException {
        PreparedStatement getCountStmt = connection.prepareStatement(String.format("SELECT count(*) as n_rows FROM %s;", tableName));

        ResultSet rsCount = getCountStmt.executeQuery();
        rsCount.next();
        int rowCount = rsCount.getInt("n_rows");
        rsCount.close();
        getCountStmt.close();

        HashMap<String, Map<String, Double>> mostFrequentValues = new HashMap<>();

        PreparedStatement getStatisticsStmt = connection.prepareStatement(String.format("SELECT * FROM pg_stats WHERE tablename=?"));
        getStatisticsStmt.setString(1, tableName);

        ResultSet rsStatistics = getStatisticsStmt.executeQuery();

        while (rsStatistics.next()) {
            HashMap<String, Double> mostFrequentValuesForColumn = new HashMap<>();

            String columnName = rsStatistics.getString("attname");
            String[] mostFrequentValsArray = (String[]) rsStatistics.getArray("most_common_vals").getArray();
            Double[] mostFrequentValsFrequenciesArray = (Double[]) rsStatistics.getArray("most_common_freqs").getArray();
            for (int i = 0; i < mostFrequentValsArray.length; i++) {
                mostFrequentValuesForColumn.put(mostFrequentValsArray[i], mostFrequentValsFrequenciesArray[i]);
            }
            mostFrequentValues.put(columnName, mostFrequentValuesForColumn);

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
