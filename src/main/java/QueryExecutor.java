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
        System.out.println(schema);
    }

    public PreparedStatement execute(PreparedStatement ps) throws SQLException {
        String query = ps.toString(); // Should work with postgres (and mysql) drivers

        ps.execute();

        return ps;
    }

    public PreparedStatement execute(String queryStr) throws SQLException, QueryConversionException {
        SQLQuery sqlQuery = new SQLQuery(queryStr, schema);

        String functionName = SQLQuery.generateFunctionName();
        String functionStr = sqlQuery.toFunction(functionName);

        PreparedStatement ps = connection.prepareStatement(functionStr);
        ps.execute();

        ResultSet rs = ps.executeQuery(String.format("SELECT %s()", functionName));

        ps.execute();

        return ps;
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
