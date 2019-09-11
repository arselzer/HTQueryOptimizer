package benchmark;

import hypergraph.Hypergraph;
import query.JoinTreeNode;

public class BenchmarkResult {
    private BenchmarkConf conf;
    private long unoptimizedRuntime;
    private long optimizedTotalRuntime;
    private long optimizedQueryRuntime;

    private int unoptimizedRows;
    private int optimizedRows;

    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;


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

    @Override
    public String toString() {
        return "BenchmarkResult{" +
                "conf=" + conf +
                ", unoptimizedRuntime=" + unoptimizedRuntime +
                ", optimizedTotalRuntime=" + optimizedTotalRuntime +
                ", optimizedQueryRuntime=" + optimizedQueryRuntime +
                ", unoptimizedRows=" + unoptimizedRows +
                ", optimizedRows=" + optimizedRows +
                ", hypergraph=" + hypergraph +
                ", joinTree=" + joinTree +
                '}';
    }
}
