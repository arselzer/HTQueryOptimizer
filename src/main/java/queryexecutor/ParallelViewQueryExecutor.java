package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParallelViewQueryExecutor implements QueryExecutor {
    @Override
    public void setTimeout(int n) {

    }

    @Override
    public ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException, TableNotFoundException {
        return null;
    }

    @Override
    public ResultSet execute(String query) throws SQLException, QueryConversionException, TableNotFoundException {
        return null;
    }
}
