import exceptions.QueryConversionException;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Properties;

public class QueryExecutorTest {
    private static final String query = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t1.a = t3.a\n" +
            "AND t1.a = t4.a\n" +
            "AND t4.e = t5.a\n" +
            "AND t5.e = t6.e\n" +
            "\n";
    @Test
    void connectAndQuery() throws SQLException, QueryConversionException {
        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        //properties.setProperty("ssl", "true");

        Connection conn = DriverManager.getConnection(url, properties);

        QueryExecutor qe = new QueryExecutor(conn);

        PreparedStatement ps = qe.execute(query);

        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }
}
