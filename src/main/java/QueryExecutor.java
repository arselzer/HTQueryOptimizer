import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryExecutor {
    private Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement execute(PreparedStatement ps) throws SQLException {
        String query = ps.toString(); // Should work with postgres and mysql drivers

        ps.execute();

        return ps;
    }

    public PreparedStatement execute(String query) throws SQLException {

        PreparedStatement ps = connection.prepareStatement(query);

        ps.execute();

        return ps;
    }
}
