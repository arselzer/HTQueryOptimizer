package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryExecutor {
    void setTimeout(int n);

    ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException, TableNotFoundException;

    ResultSet executeBoolean(PreparedStatement preparedStatement) throws SQLException, QueryConversionException, TableNotFoundException;

    ResultSet execute(String query) throws SQLException, QueryConversionException, TableNotFoundException;

    ResultSet executeBoolean(String query) throws SQLException, QueryConversionException, TableNotFoundException;
}
