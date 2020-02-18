package benchmark;

public class SummarizedResultsCSVGenerator {

    private static String[] columns = new String[]{
            "db",
            "query",
            "algorithm",
            "size",
            "run",
            "origRuntime",
            "optQueryRuntime",
            "optTotalRuntime",
            "origRows",
            "optRows",
            "origCols",
            "optCols",
            "treeHeight",
            "treeNodes",
            "origTimeout",
            "optTimeout",
            "correct"
    };

    private String csv = "";

    public SummarizedResultsCSVGenerator() {
        csv = String.join(",", columns) + "\n";
    }

    public void addResult(BenchmarkResult result) {
        csv += result.getConf().getDb() + "," +
                result.getConf().getQuery() + "," +
                result.getConf().getDecompositionOptions().getAlgorithm().name() + "," +
                result.getConf().getDbSize() + "," +
                result.getConf().getRun() + "," +
                result.getUnoptimizedRuntime() + "," +
                result.getOptimizedQueryRuntime() + "," +
                result.getOptimizedTotalRuntime() + "," +
                result.getUnoptimizedRows() + "," +
                result.getOptimizedRows() + "," +
                result.getUnoptimizedColumns() + "," +
                result.getOptimizedColumns() + "," +
                result.getJoinTree().getHeight() + "," +
                result.getJoinTree().getNumberOfNodes() + "," +
                (result.isUnoptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.isOptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.isOptimizedResultCorrect() ? 1 : 0) +
                "\n";

    }

    public String getCSV() {
        return csv;
    }
}
