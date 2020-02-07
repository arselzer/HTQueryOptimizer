package benchmark;

public class SummarizedResultsCSVGenerator {

    private static String[] columns = new String[] {
            "db",
            "query",
            "algorithm",
            "run",
            "unoptimizedRuntime",
            "optimizedQueryRuntime",
            "optimizedTotalRuntime",
            "unoptimizedRows",
            "optimizedRows"

    };

    private String csv = "";

    public SummarizedResultsCSVGenerator() {
        csv = String.join(",", columns) + "\n";
    }

    public void addResult(BenchmarkResult result) {
        csv += result.getConf().getDb() + "," +
                result.getConf().getQuery() + "," +
                result.getConf().getDecompositionOptions().getAlgorithm().name() + "," +
                result.getConf().getRun() + "," +
                result.getUnoptimizedRuntime() + "," +
                result.getOptimizedQueryRuntime() + "," +
                result.getOptimizedTotalRuntime() + "," +
                result.getUnoptimizedRows() + "," +
                result.getOptimizedRows() +
                "\n";

    }

    public String getCSV() {
        return csv;
    }
}
