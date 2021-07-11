package queryexecutor;

import java.util.List;

public class ExecutionStatistics {
    private String queryName;
    private List<String> queryStrings;
    private long runtime;

    public ExecutionStatistics(String queryName, List<String> queryStrings, long runtime) {
        this.queryName = queryName;
        this.queryStrings = queryStrings;
        this.runtime = runtime;
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
}
