package queryexecutor;

import java.sql.ResultSet;
import java.util.List;

public class StatisticsResultSet {
    private ResultSet resultSet;
    private List<ExecutionStatistics> statistics;

    public StatisticsResultSet(ResultSet resultSet, List<ExecutionStatistics> statistics) {
        this.resultSet = resultSet;
        this.statistics = statistics;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public List<ExecutionStatistics> getStatistics() {
        return statistics;
    }
}
