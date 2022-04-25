package queryexecutor;

import query.JoinTreeNode;

import java.util.List;
import java.util.Map;

public class AnalyzeExecutionStatistics extends ExecutionStatistics {
    private List<String> analyzeJSONs;
    private List<String> analyzeJSONQueryStrings;

    public AnalyzeExecutionStatistics(String queryName, List<String> queryStrings, long runtime,
                                      Map<String, Integer> queryRows,
                                      List<String> analyzeJSONs, List<String> analyzeJSONQueryStrings) {
        super(queryName, queryStrings, runtime, queryRows);

        this.analyzeJSONs = analyzeJSONs;
        this.analyzeJSONQueryStrings = analyzeJSONQueryStrings;
    }

    public List<String> getAnalyzeJSONs() {
        return analyzeJSONs;
    }

    public List<String> getAnalyzeJSONQueryStrings() {
        return analyzeJSONQueryStrings;
    }
}
