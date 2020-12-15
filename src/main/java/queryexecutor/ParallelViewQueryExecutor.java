package queryexecutor;

import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import query.SQLQuery;
import schema.Table;
import schema.TableStatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ParallelViewQueryExecutor extends ViewQueryExecutor {
    public ParallelViewQueryExecutor(Connection connection) throws SQLException {
        super(connection);
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

        String functionName = SQLQuery.generateFunctionName();
        String functionStr = sqlQuery.toFunction(functionName);

        // Save hypergraph and join tree for benchmarks and analysis
        this.hypergraph = sqlQuery.getHypergraph();
        this.joinTree = sqlQuery.getJoinTree();
        this.generatedFunction = functionStr;

        long startTime = System.currentTimeMillis();

        try (PreparedStatement psFunction = connection.prepareStatement(functionStr)) {
            psFunction.execute();

            PreparedStatement psSelect = connection.prepareStatement(
                    String.format("SELECT * FROM %s();", functionName),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            if (timeout != null) {
                psSelect.setQueryTimeout(timeout);
            }
            psSelect.closeOnCompletion();
            ResultSet rs = psSelect.executeQuery();

            queryRunningTime = System.currentTimeMillis() - startTime;

            //PreparedStatement psSelectFromView = connection.prepareStatement(String.format("SELECT * FROM %s", finalTableName));
            //ResultSet rs = psSelectFromView.executeQuery();

            // TODO maybe keep the function over several calls for performance
            PreparedStatement psDelete = connection.prepareStatement(String.format("DROP FUNCTION %s;", functionName));
            psDelete.execute();
            psDelete.close();

            //enableMergeJoin();
            // TODO disabling merge join alters global db state ? - maybe isolate it if possible

            return rs;
        }
    }

}
