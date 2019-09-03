import exceptions.QueryConversionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Properties;

public class QueryExecutorTest {
    private static Connection conn;

    private static final String starQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t1.a = t3.a\n" +
            "AND t1.a = t4.a\n" +
            "AND t4.e = t5.a\n" +
            "AND t5.e = t6.e\n" +
            "\n";

    private static final String triangleQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t2.c = t3.a\n" +
            "AND t3.d = t1.b";

    @BeforeAll
    static void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        //properties.setProperty("ssl", "true");

        conn = DriverManager.getConnection(url, properties);
    }

    @Test
    void connectAndQuery() throws SQLException, QueryConversionException {
        QueryExecutor qe = new QueryExecutor(conn);

        ResultSet rs = qe.execute(starQuery);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void triangleQuery() throws SQLException, QueryConversionException {
        QueryExecutor qe = new QueryExecutor(conn);

        ResultSet rs = qe.execute(triangleQuery);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }
}
