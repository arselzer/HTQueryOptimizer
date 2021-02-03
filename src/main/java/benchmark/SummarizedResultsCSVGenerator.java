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
            "treeWidth",
            "origTimeout",
            "optTimeout",
            "parallelized",
            "threads",
            "correct",
            "balancednessFactor",
            "vertexBagSizeMin",
            "vertexBagSizeMax",
            "vertexBagSizeMean",
            "vertexBagSizeSum",
            "vertexBagSizeSdev",
            "edgeBagSizeMin",
            "edgeBagSizeMax",
            "edgeBagSizeMean",
            "edgeBagSizeSum",
            "edgeBagSizeSdev"
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
                result.getJoinTree().getHypertreeWidth() + "," +
                (result.isUnoptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.isOptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.getConf().isParallel() ? 1 : 0) + "," +
                (result.getConf().getThreadCount() == null ? -1 : result.getConf().getThreadCount()) + "," +
                (result.isOptimizedResultCorrect() ? 1 : 0) + "," +
                result.getJoinTree().getBalancednessFactor() + "," +
                result.getJoinTree().getVertexBagSizeStatistics().getMin() + "," +
                result.getJoinTree().getVertexBagSizeStatistics().getMax() + "," +
                result.getJoinTree().getVertexBagSizeStatistics().getMean() + "," +
                result.getJoinTree().getVertexBagSizeStatistics().getSum() + "," +
                result.getJoinTree().getVertexBagSizeStatistics().getStandardDeviation() + "," +
                result.getJoinTree().getEdgeBagSizeStatistics().getMin() + "," +
                result.getJoinTree().getEdgeBagSizeStatistics().getMax() + "," +
                result.getJoinTree().getEdgeBagSizeStatistics().getMean() + "," +
                result.getJoinTree().getEdgeBagSizeStatistics().getSum() + "," +
                result.getJoinTree().getEdgeBagSizeStatistics().getStandardDeviation() +
                "\n";

    }

    public String getCSV() {
        return csv;
    }
}
