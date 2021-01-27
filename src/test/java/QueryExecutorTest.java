import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import queryexecutor.ConnectionPool;
import queryexecutor.QueryExecutor;
import queryexecutor.UnoptimizedQueryExecutor;
import queryexecutor.TempTableQueryExecutor;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class QueryExecutorTest {
    private static final String starQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t1.a = t3.a\n" +
            "AND t1.a = t4.a\n" +
            "AND t4.b = t5.a\n" +
            "AND t5.b = t6.a";
    private static final String triangleQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t2.b = t3.a\n" +
            "AND t3.b = t1.b";
    private static final String triangleStarQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t2.b = t3.a\n" +
            "AND t3.b = t1.b\n" +
            "AND t3.b = t4.a";
    private static final String multipleCyclesQuery = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6, t7, t8, t9, t10\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t2.b = t3.a\n" +
            "AND t3.b = t1.b\n" +
            "AND t4.a = t5.a\n" +
            "AND t5.b = t6.a\n" +
            "AND t6.b = t1.b\n" +
            "AND t7.a = t8.a\n" +
            "AND t8.b = t9.a\n" +
            "AND t9.b = t10.a\n" +
            "AND t10.b = t7.b\n" +
            "AND t1.a = t10.a\n" +
            "AND t6.a = t9.a;";
    private static final String multipleCyclesQuery2 = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11\n" +
            "WHERE t1.a = t2.a\n" +
            "AND t2.b = t3.a\n" +
            "AND t3.b = t4.a\n" +
            "AND t4.b = t1.b\n" +
            "AND t4.a = t7.a\n" +
            "AND t7.b = t8.a\n" +
            "AND t8.a = t9.b\n" +
            "AND t9.a = t10.b\n" +
            "AND t10.b = t8.b\n" +
            "AND t10.a = t11.a\n" +
            "AND t3.b = t11.b\n" +
            "AND t4.a = t11.a;";
    private static final String multipleCyclesQuery3 = "SELECT *\n" +
            "FROM t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11\n" +
            "WHERE t1.b = t2.a -- cycle 1\n" +
            "AND t2.b = t3.a\n" +
            "AND t3.b = t4.a\n" +
            "AND t4.b = t5.a\n" +
            "AND t5.b = t1.a\n" +
            "AND t6.b = t7.a -- cycle 2\n" +
            "AND t7.b = t8.a\n" +
            "AND t8.b = t6.a\n" +
            "AND t7.a = t3.a -- connection of cycles\n" +
            "AND t1.b = t6.a -- connection\n" +
            "AND t9.a = t7.a -- chain\n" +
            "AND t9.b = t10.a\n" +
            "AND t11.a = t5.b\n" +
            "AND t11.b = t8.a;";
    private static final String chainQuery = "select *\n" +
            "from t1, t2, t3, t4, t5, t6\n" +
            "where t1.a = t2.a\n" +
            "and t2.a = t3.a\n" +
            "and t3.a = t4.a\n" +
            "AND t4.b = t5.a;";
    private static Connection conn;
    private static ConnectionPool connPool;

    @BeforeEach
    void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        //properties.setProperty("ssl", "true");

        conn = DriverManager.getConnection(url, properties);
        connPool = new ConnectionPool(url, properties);
    }

    @Test
    void connectAndQuery() throws SQLException, QueryConversionException, TableNotFoundException {
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        ResultSet rs = qe.execute(starQuery);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void triangleQuery() throws SQLException, QueryConversionException, TableNotFoundException {
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        ResultSet rs = qe.execute(triangleQuery);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void triangleStarQuery() throws SQLException, QueryConversionException, TableNotFoundException {
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        ResultSet rs = qe.execute(triangleStarQuery);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void multipleCyclesQuery() throws SQLException, QueryConversionException, TableNotFoundException {
        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        long startTime = System.currentTimeMillis();
        ResultSet rs = qe.execute(multipleCyclesQuery);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Time elapsed: %d ms\n", totalTime);

        startTime = System.currentTimeMillis();
        uoqe.execute(multipleCyclesQuery);
        totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Time elapsed: %d ms\n", totalTime);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void multipleCyclesQuery2() throws SQLException, QueryConversionException, IOException, InterruptedException, TableNotFoundException {
        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        long startTime = System.currentTimeMillis();
        ResultSet rs = qe.execute(multipleCyclesQuery2);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Time elapsed: %d ms\n", totalTime);

        qe.getHypergraph().displayPDF();

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void multipleCyclesQuery3() throws SQLException, QueryConversionException, TableNotFoundException {
        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        long startTime = System.currentTimeMillis();
        ResultSet rs = qe.execute(multipleCyclesQuery3);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Time elapsed: %d ms\n", totalTime);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }

    @Test
    void chainQuery() throws SQLException, QueryConversionException, TableNotFoundException {
        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        TempTableQueryExecutor qe = new TempTableQueryExecutor(connPool);

        long startTime = System.currentTimeMillis();
        ResultSet rs = qe.execute(chainQuery);
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("Time elapsed: %d ms\n", totalTime);

        int i = 0;
        while (rs.next() && i < 100) {
            System.out.println(rs.getString(1));
            i++;
        }
    }
}
