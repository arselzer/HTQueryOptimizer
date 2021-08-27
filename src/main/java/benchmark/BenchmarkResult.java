package benchmark;

import hypergraph.Hypergraph;
import query.JoinTreeNode;
import queryexecutor.ExecutionStatistics;

import java.util.List;

public class BenchmarkResult {
    private BenchmarkConf conf;
    private long unoptimizedRuntime;
    private long optimizedTotalRuntime;
    private long joinTreeComputationRuntime;
    private long hypergraphComputationRuntime;
    private long optimizedQueryRuntime;
    private long dropTime;
    private long totalPreprocessingTime;
    private long[] stageRuntimes;

    private boolean optimizedQueryTimeout = false;
    private boolean unoptimizedQueryTimeout = false;
    private boolean optimizedResultCorrect = true;

    private int unoptimizedColumns;
    private int optimizedColumns;

    private int unoptimizedRows;
    private int optimizedRows;

    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;

    private String query;
    private String generatedQuery;

    private String analyzeJSON;

    private List<ExecutionStatistics> executionStatistics;

    BenchmarkResult(BenchmarkConf conf) {
        this.conf = conf;
    }

    public BenchmarkConf getConf() {
        return conf;
    }

    public void setConf(BenchmarkConf conf) {
        this.conf = conf;
    }

    public long getUnoptimizedRuntime() {
        return unoptimizedRuntime;
    }

    public void setUnoptimizedRuntime(long unoptimizedRuntime) {
        this.unoptimizedRuntime = unoptimizedRuntime;
    }

    public long getOptimizedTotalRuntime() {
        return optimizedTotalRuntime;
    }

    public void setOptimizedTotalRuntime(long optimizedTotalRuntime) {
        this.optimizedTotalRuntime = optimizedTotalRuntime;
    }

    public long getOptimizedQueryRuntime() {
        return optimizedQueryRuntime;
    }

    public void setOptimizedQueryRuntime(long optimizedQueryRuntime) {
        this.optimizedQueryRuntime = optimizedQueryRuntime;
    }

    public long getDropTime() {
        return dropTime;
    }

    public void setDropTime(long dropTime) {
        this.dropTime = dropTime;
    }

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public void setHypergraph(Hypergraph hypergraph) {
        this.hypergraph = hypergraph;
    }

    public JoinTreeNode getJoinTree() {
        return joinTree;
    }

    public void setJoinTree(JoinTreeNode joinTree) {
        this.joinTree = joinTree;
    }

    public int getUnoptimizedColumns() {
        return unoptimizedColumns;
    }

    public void setUnoptimizedColumns(int unoptimizedColumns) {
        this.unoptimizedColumns = unoptimizedColumns;
    }

    public int getOptimizedColumns() {
        return optimizedColumns;
    }

    public void setOptimizedColumns(int optimizedColumns) {
        this.optimizedColumns = optimizedColumns;
    }

    public int getUnoptimizedRows() {
        return unoptimizedRows;
    }

    public void setUnoptimizedRows(int unoptimizedRows) {
        this.unoptimizedRows = unoptimizedRows;
    }

    public int getOptimizedRows() {
        return optimizedRows;
    }

    public void setOptimizedRows(int optimizedRows) {
        this.optimizedRows = optimizedRows;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isOptimizedQueryTimeout() {
        return optimizedQueryTimeout;
    }

    public void setOptimizedQueryTimeout(boolean optimizedQueryTimeout) {
        this.optimizedQueryTimeout = optimizedQueryTimeout;
    }

    public boolean isOptimizedResultCorrect() {
        return optimizedResultCorrect;
    }

    public void setOptimizedResultCorrect(boolean optimizedResultCorrect) {
        this.optimizedResultCorrect = optimizedResultCorrect;
    }

    public boolean isUnoptimizedQueryTimeout() {
        return unoptimizedQueryTimeout;
    }

    public void setUnoptimizedQueryTimeout(boolean unoptimizedQueryTimeout) {
        this.unoptimizedQueryTimeout = unoptimizedQueryTimeout;
    }

    public String getGeneratedQuery() {
        return generatedQuery;
    }

    public void setGeneratedQuery(String generatedQuery) {
        this.generatedQuery = generatedQuery;
    }

    public String getAnalyzeJSON() {
        return analyzeJSON;
    }

    public void setAnalyzeJSON(String analyzeJSON) {
        this.analyzeJSON = analyzeJSON;
    }

    public List<ExecutionStatistics> getExecutionStatistics() {
        return executionStatistics;
    }

    public void setExecutionStatistics(List<ExecutionStatistics> executionStatistics) {
        this.executionStatistics = executionStatistics;
    }

    public long getJoinTreeComputationRuntime() {
        return joinTreeComputationRuntime;
    }

    public void setJoinTreeComputationRuntime(long joinTreeComputationRuntime) {
        this.joinTreeComputationRuntime = joinTreeComputationRuntime;
    }

    public long getHypergraphComputationRuntime() {
        return hypergraphComputationRuntime;
    }

    public void setHypergraphComputationRuntime(long hypergraphComputationRuntime) {
        this.hypergraphComputationRuntime = hypergraphComputationRuntime;
    }

    public long getTotalPreprocessingTime() {
        return totalPreprocessingTime;
    }

    public void setTotalPreprocessingTime(long totalPreprocessingTime) {
        this.totalPreprocessingTime = totalPreprocessingTime;
    }

    public long[] getStageRuntimes() {
        return stageRuntimes;
    }

    public void setStageRuntimes(long[] stageRuntimes) {
        this.stageRuntimes = stageRuntimes;
    }

    @Override
    public String toString() {
        return "BenchmarkResult{" +
                "conf=" + conf +
                ", unoptimizedRuntime=" + unoptimizedRuntime +
                ", optimizedTotalRuntime=" + optimizedTotalRuntime +
                ", optimizedQueryRuntime=" + optimizedQueryRuntime +
                ", optimizedQueryTimeout=" + optimizedQueryTimeout +
                ", unoptimizedQueryTimeout=" + unoptimizedQueryTimeout +
                ", optimizedResultCorrect=" + optimizedResultCorrect +
                ", unoptimizedColumns=" + unoptimizedColumns +
                ", optimizedColumns=" + optimizedColumns +
                ", unoptimizedRows=" + unoptimizedRows +
                ", optimizedRows=" + optimizedRows +
                ", hypergraph=" + hypergraph +
                ", joinTree=" + joinTree +
                ", query='" + query + '\'' +
                ", generatedQuery='" + generatedQuery + '\'' +
                '}';
    }
}
