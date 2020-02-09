package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryExecutor {
    public void setTimeout(int n);

    public ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException, TableNotFoundException;

    public ResultSet execute(String query) throws SQLException, QueryConversionException, TableNotFoundException;
}
