package queryexecutor;

import query.JoinTreeNode;

import java.util.List;

public class AnalyzeExecutionStatistics extends ExecutionStatistics {
    private List<String> analyzeJSONs;
    private List<String> analyzeJSONQueryStrings;

    public AnalyzeExecutionStatistics(String queryName, List<String> queryStrings, long runtime,
                                      List<String> analyzeJSONs, List<String> analyzeJSONQueryStrings) {
        super(queryName, queryStrings, runtime);

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
