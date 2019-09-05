package queryexecutor;

import exceptions.QueryConversionException;
import schema.DBSchema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnoptimizedQueryExecutor implements QueryExecutor {
    private Connection connection;

    public UnoptimizedQueryExecutor(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException {
        return preparedStatement.executeQuery();
    }

    @Override
    public ResultSet execute(String query) throws SQLException, QueryConversionException {
        return connection.prepareStatement(query).executeQuery();
    }
}
