package queryexecutor;

import exceptions.QueryConversionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface QueryExecutor {
    public ResultSet execute(PreparedStatement preparedStatement) throws SQLException, QueryConversionException;
    public ResultSet execute(String query) throws SQLException, QueryConversionException;
}
