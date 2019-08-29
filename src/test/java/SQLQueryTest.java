import exceptions.QueryConversionException;
import hypergraph.Hypergraph;
import org.junit.jupiter.api.Test;
import query.SQLQuery;

public class SQLQueryTest {
    private static final String schemaString = "CREATE TABLE Flights(\n" +
            "    id  int,\n" +
            "    s   varchar(255),\n" +
            "    d   varchar(255)\n" +
            ");\n";
    private static final String queryString = "SELECT *\n" +
            "FROM Flights f1, Flights f2, Flights f3\n" +
            "WHERE f1.d=f2.s AND f2.d=f3.s AND f3.d=f1.s AND f1.s='SUF';\n";

    @Test
    void parseQueryAndSchema() throws QueryConversionException {

        SQLQuery query = new SQLQuery(queryString, schemaString);

        Hypergraph hg = query.toHypergraph();
        System.out.println(hg.getEdges());

        System.out.println(hg);
        System.out.println(hg.toDTL());
        System.out.println(hg.getEquivalenceMapping());
    }
}
