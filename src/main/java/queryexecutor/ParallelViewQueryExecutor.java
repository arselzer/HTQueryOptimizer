package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import query.ParallelQueryExecution;
import query.SQLQuery;
import schema.Table;
import schema.TableStatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParallelViewQueryExecutor extends ViewQueryExecutor {
    private ConnectionPool connectionPool;

    public ParallelViewQueryExecutor(ConnectionPool connectionPool) throws SQLException {
        super(connectionPool);
        this.connectionPool = connectionPool;
    }

    @Override
    public ResultSet execute(String queryStr) throws SQLException, QueryConversionException, TableNotFoundException {
        sqlQuery = new SQLQuery(queryStr, schema);
        sqlQuery.setDecompositionOptions(decompositionOptions);

        Map<String, TableStatistics> statisticsMap = new HashMap<>();
        for (Table table: schema.getTables()) {
            statisticsMap.put(table.getName(), extractTableStatistics(table.getName()));
        }
        sqlQuery.setStatistics(statisticsMap);

        ParallelQueryExecution queryExecution = sqlQuery.toParallelExecution();

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();

        long startTime = System.currentTimeMillis();

        try {
            for (List<String> layer : queryExecution.getSqlStatements()) {
                layer.parallelStream().forEach(query -> {
                    System.out.println("-- executing query: \n" + query);
                    try {
                        Connection conn = connectionPool.getConnection();
                        PreparedStatement ps = conn.prepareStatement(query);
                        ps.execute();
                        connectionPool.returnConnection(conn);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println("-- time elapsed: " + (System.currentTimeMillis() - startTime));
            }
        }
        catch (RuntimeException e) {
            // Remove temp views / tables TODO refactor to avoid duplicate code
            for (String dropStatement : queryExecution.getDropStatements().toList()) {
                connection.prepareStatement(dropStatement).execute();
            }

            // It's not possible to throw a checked exception in a lambda function
            // TODO write a custom SQLRuntimeException
            throw new SQLException(e);
        }

        PreparedStatement psSelect = connection.prepareStatement(
                String.format("SELECT * FROM %s;", queryExecution.getFinalSelectName()),
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        if (timeout != null) {
            psSelect.setQueryTimeout(timeout);
        }
        psSelect.closeOnCompletion();
        ResultSet rs = psSelect.executeQuery();

        queryRunningTime = System.currentTimeMillis() - startTime;

        System.out.println("total time elapsed: " + queryRunningTime);

        // Remove temp views / tables
        for (String dropStatement : queryExecution.getDropStatements().toList()) {
            connection.prepareStatement(dropStatement).execute();
        }

        return rs;
    }
}
