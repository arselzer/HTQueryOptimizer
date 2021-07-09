package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnoptimizedQueryExecutor implements QueryExecutor {
    private Connection connection;
    private Integer timeout = null;

    public UnoptimizedQueryExecutor(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public void setTimeout(int n) {
        timeout = n;
    }

    @Override
    public ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException {
        if (timeout != null) {
            preparedStatement.setQueryTimeout(timeout);
        }
        preparedStatement.closeOnCompletion();
        return preparedStatement.executeQuery();
    }

    @Override
    public ResultSet executeBoolean(PreparedStatement preparedStatement) throws SQLException, QueryConversionException, TableNotFoundException {
        return executeBoolean(preparedStatement.toString());
    }

    public ResultSet execute(String query, boolean analyze) throws SQLException, QueryConversionException {
        PreparedStatement ps = connection.prepareStatement(
                (analyze ? "EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) " : "") + query,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        return execute(ps);
    }

    @Override
    public ResultSet execute(String query) throws SQLException, QueryConversionException {
        return execute(query, false);
    }

    public ResultSet executeBoolean(String query, boolean analyze) throws SQLException, QueryConversionException, TableNotFoundException {
        String booleanQuery = (analyze ? "EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) " : "")
                + "SELECT * FROM (" + query.replaceFirst(";\\s*$", "") + ") sq LIMIT 1;";

        return execute(booleanQuery);
    }

    @Override
    public ResultSet executeBoolean(String query) throws SQLException, QueryConversionException, TableNotFoundException {
        return executeBoolean(query, false);
    }
}
