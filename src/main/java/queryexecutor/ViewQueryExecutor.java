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

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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

        String functionName = SQLQuery.generateFunctionName();
        String functionStr = sqlQuery.toFunction(functionName);

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();
        this.generatedFunction = functionStr;

        long startTime = System.currentTimeMillis();

        try (PreparedStatement psFunction = connection.prepareStatement(functionStr)) {
            psFunction.execute();

            PreparedStatement psSelect = connection.prepareStatement(String.format("SELECT * FROM %s();", functionName), ResultSet.TYPE_SCROLL_INSENSITIVE,
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

    private void extractSchema() throws SQLException {
        schema = new DBSchema();

        DatabaseMetaData metaData = connection.getMetaData();

        ResultSet rs = metaData.getTables(connection.getCatalog(), "public", "%", new String[]{"TABLE"});
        //ResultSet rs = metaData.getTables(null, null, "%", null);

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
