import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.Hypergraph;
import org.junit.jupiter.api.Test;
import schema.Column;
import schema.DBSchema;
import query.SQLQuery;
import schema.Table;
import java.util.List;

import java.util.LinkedList;

public class SQLQueryTest {
    private static final String schemaString = "CREATE TABLE Flights(\n" +
            "    id  int,\n" +
            "    s   varchar(255),\n" +
            "    d   varchar(255)\n" +
            ");\n";

    private DBSchema schema = new DBSchema();

    private static final String queryString = "SELECT *\n" +
            "FROM Flights f1, Flights f2, Flights f3\n" +
            "WHERE f1.d=f2.s AND f2.d=f3.s AND f3.d=f1.s AND f1.s='SUF';\n";
    private static final String queryString2 = "SELECT f1.d, f2.d,f1.s\n" +
            "FROM Flights f1, Flights f2, Flights f3\n" +
            "WHERE f1.d=f2.s AND f2.d=f3.s AND f3.d=f1.s AND f1.s='SUF';\n";

    @Test
    void parseQueryAndSchema() throws QueryConversionException, TableNotFoundException {

        List<Table> tables = new LinkedList<>();
        Table flights = new Table();
        List<Column> columns = new LinkedList<>();
        columns.add(new Column("id", "int"));
        columns.add(new Column("s", "varchar"));
        columns.add(new Column("d", "varchar"));
        flights.setName("Flights");
        flights.setColumns(columns);
        tables.add(flights);
        schema.setTables(tables);

        SQLQuery query = new SQLQuery(queryString, schema);

        Hypergraph hg = query.toHypergraph();
        System.out.println(hg.getEdges());

        System.out.println(hg);
        System.out.println(hg.toDTL());
        System.out.println(hg.getColumnToVariableMapping());
    }

    @Test
    void parseQueryAndSchema2() throws QueryConversionException, TableNotFoundException {
        List<Table> tables = new LinkedList<>();
        Table flights = new Table();
        List<Column> columns = new LinkedList<>();
        columns.add(new Column("id", "int"));
        columns.add(new Column("s", "varchar"));
        columns.add(new Column("d", "varchar"));
        flights.setName("Flights");
        flights.setColumns(columns);
        tables.add(flights);
        schema.setTables(tables);

        SQLQuery query = new SQLQuery(queryString2, schema);

        Hypergraph hg = query.toHypergraph();
        System.out.println(hg.getEdges());

        System.out.println(hg);
        System.out.println(hg.toDTL());
        System.out.println(hg.getColumnToVariableMapping());
    }
}
