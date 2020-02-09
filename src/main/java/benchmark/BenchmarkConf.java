package benchmark;

import hypergraph.DecompositionOptions;

public class BenchmarkConf {
    private String db;
    private String query;
    private String suffix;
    private Integer run;
    private DecompositionOptions decompositionOptions;
    // Timeout in seconds
    private Integer queryTimeout = null;

    private Boolean skipRows = true;

    public BenchmarkConf(String db, String query, String suffix, DecompositionOptions decompositionOptions) {
        this.db = db;
        this.query = query;
        this.suffix = suffix;
        this.decompositionOptions = decompositionOptions;
    }

    public BenchmarkConf(String db, String query, String suffix,
                         DecompositionOptions decompositionOptions, Integer timeout, Integer run) {
        this.db = db;
        this.query = query;
        this.suffix = suffix;
        this.decompositionOptions = decompositionOptions;
        this.queryTimeout = timeout;
        this.run = run;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Integer getRun() {
        return run;
    }

    public void setRun(Integer run) {
        this.run = run;
    }

    public Boolean getSkipRows() {
        return skipRows;
    }

    public void setSkipRows(Boolean skipRows) {
        this.skipRows = skipRows;
    }

    public DecompositionOptions getDecompositionOptions() {
        return decompositionOptions;
    }

    public void setDecompositionOptions(DecompositionOptions decompositionOptions) {
        this.decompositionOptions = decompositionOptions;
    }

    @Override
    public String toString() {
        return "BenchmarkConf{" +
                "db='" + db + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
