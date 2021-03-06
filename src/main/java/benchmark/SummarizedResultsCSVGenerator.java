package benchmark;

import hypergraph.Hypergraph;
import query.JoinTreeNode;

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
            "origTimeout",
            "optTimeout",
            "parallelized",
            "threads",
            "boolean",
            "correct",
            "hgDegree",
            "hgVCDimension",
            "hgBIP",
            "treeHeight",
            "treeNodes",
            "treeWidth",
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
        Hypergraph hg = result.getHypergraph();
        JoinTreeNode jt = result.getJoinTree();
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
                (result.isUnoptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.isOptimizedQueryTimeout() ? 1 : 0) + "," +
                (result.getConf().isParallel() ? 1 : 0) + "," +
                (result.getConf().getThreadCount() == null ? -1 : result.getConf().getThreadCount()) + "," +
                (result.getConf().isBooleanQuery() ? 1 : 0) + "," +
                (result.isOptimizedResultCorrect() ? 1 : 0) + "," +
                (hg == null ? "" : hg.getDegree()) + "," +
                (hg == null ? "" : hg.getVCDimension()) + "," +
                (hg == null ? "" : hg.getBIP()) + "," +
                (jt == null ? "" : jt.getHeight()) + "," +
                (jt == null ? "" : jt.getNumberOfNodes()) + "," +
                (jt == null ? "" : jt.getHypertreeWidth()) + "," +
                (jt == null ? "" : jt.getBalancednessFactor()) + "," +
                (jt == null ? "" : jt.getVertexBagSizeStatistics().getMin()) + "," +
                (jt == null ? "" : jt.getVertexBagSizeStatistics().getMax()) + "," +
                (jt == null ? "" : jt.getVertexBagSizeStatistics().getMean()) + "," +
                (jt == null ? "" : jt.getVertexBagSizeStatistics().getSum()) + "," +
                (jt == null ? "" : jt.getVertexBagSizeStatistics().getStandardDeviation()) + "," +
                (jt == null ? "" : jt.getEdgeBagSizeStatistics().getMin()) + "," +
                (jt == null ? "" : jt.getEdgeBagSizeStatistics().getMax()) + "," +
                (jt == null ? "" : jt.getEdgeBagSizeStatistics().getMean()) + "," +
                (jt == null ? "" : jt.getEdgeBagSizeStatistics().getSum()) + "," +
                (jt == null ? "" : jt.getEdgeBagSizeStatistics().getStandardDeviation()) +
                "\n";

    }

    public String getCSV() {
        return csv;
    }
}
