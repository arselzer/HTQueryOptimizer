import at.ac.tuwien.dbai.hgtools.sql2hg.Schema;
import exceptions.QueryConversionException;
import hypergraph.Hypergraph;
import org.postgresql.copy.CopyManager;
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

        List<Table> schemaTables = new LinkedList<>();
        while (rs.next()) {
            String tableName = rs.getString(3);
            if (!tableName.startsWith("sql_")) {
                // Ignore internal tables

                Table table = new Table();
                table.setName(tableName);

                ResultSet rs2 = metaData.getColumns(null, null, tableName, null);
                LinkedList<String> tableColumns = new LinkedList<>();
                while (rs2.next()) {
                    tableColumns.add(rs2.getString(4));
                }
                table.setColumns(tableColumns);
                schemaTables.add(table);
            }
        }

        schema.setTables(schemaTables);
    }
}
