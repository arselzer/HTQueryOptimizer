import at.ac.tuwien.dbai.hgtools.sql2hg.Schema;
import exceptions.QueryConversionException;
import hypergraph.Hypergraph;
import org.postgresql.copy.CopyManager;
import query.Column;
import query.DBSchema;
import query.SQLQuery;
import query.Table;

import java.util.List;
import java.sql.*;
import java.util.LinkedList;

public class QueryExecutor {
    private Connection connection;
    private DBSchema schema;

    public QueryExecutor(Connection connection) throws SQLException {
        this.connection = connection;

        extractSchema();
    }

    public ResultSet execute(PreparedStatement ps) throws SQLException, QueryConversionException {
        String query = ps.toString(); // Should work with postgres (and mysql) drivers

        return execute(query);
    }

    public ResultSet execute(String queryStr) throws SQLException, QueryConversionException {
        SQLQuery sqlQuery = new SQLQuery(queryStr, schema);
        //System.out.println(sqlQuery.toHypergraph().toJoinTree());

        String functionName = SQLQuery.generateFunctionName();
        String functionStr = sqlQuery.toFunction(functionName);
        System.out.println(functionStr);

        PreparedStatement psFunction = connection.prepareStatement(functionStr);
        psFunction.execute();

        PreparedStatement psSelect = connection.prepareStatement(String.format("SELECT %s();", functionName));
        ResultSet rs = psSelect.executeQuery();

        // TODO maybe keep the function over several calls for performance
        PreparedStatement psDelete = connection.prepareStatement(String.format("DROP FUNCTION %s;", functionName));
        psDelete.execute();

        return rs;
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
