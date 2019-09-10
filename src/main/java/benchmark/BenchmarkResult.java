package benchmark;

public class BenchmarkResult {
    private BenchmarkConf conf;
    private long unoptimizedRuntime;
    private long optimizedRuntime;

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

    public long getOptimizedRuntime() {
        return optimizedRuntime;
    }

    public void setOptimizedRuntime(long optimizedRuntime) {
        this.optimizedRuntime = optimizedRuntime;
    }

    @Override
    public String toString() {
        return "BenchmarkResult{" +
                "conf=" + conf +
                ", unoptimizedRuntime=" + unoptimizedRuntime +
                ", optimizedRuntime=" + optimizedRuntime +
                '}';
    }
}
