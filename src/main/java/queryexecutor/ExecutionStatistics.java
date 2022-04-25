package queryexecutor;

import java.util.List;
import java.util.Map;

public class ExecutionStatistics {
    private String queryName;
    private List<String> queryStrings;
    private long runtime;

    private Map<String, Integer> queryRows;

    public ExecutionStatistics(String queryName, List<String> queryStrings, long runtime,
                               Map<String, Integer> queryRows) {
        this.queryName = queryName;
        this.queryStrings = queryStrings;
        this.runtime = runtime;
        this.queryRows = queryRows;
    }

    public String getQueryName() {
        return queryName;
    }

    public List<String> getQueryStrings() {
        return queryStrings;
    }

    public long getRuntime() {
        return runtime;
    }

    public Map<String, Integer> getQueryRows() {
        return queryRows;
    }
}
