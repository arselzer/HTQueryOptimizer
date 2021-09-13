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
    private Integer dbSize = 1;
    private boolean parallel = true;
    private Boolean skipRows = true;
    private Integer threadCount = null;
    private boolean booleanQuery = false;
    private boolean useStatistics = true;
    private boolean isFirstRun = false;

    public BenchmarkConf(String db, String query, String suffix, DecompositionOptions decompositionOptions) {
        this.db = db;
        this.query = query;
        this.suffix = suffix;
        this.decompositionOptions = decompositionOptions;
    }

    public BenchmarkConf(String db, String query, String suffix,
                         DecompositionOptions decompositionOptions, Integer timeout, Integer run, Integer dbSize,
                         boolean parallel, Integer threadCount, boolean booleanQuery, boolean useStatistics,
                         boolean isFirstRun) {
        this.db = db;
        this.query = query;
        this.suffix = suffix;
        this.decompositionOptions = decompositionOptions;
        this.queryTimeout = timeout;
        this.run = run;
        this.dbSize = dbSize;
        this.parallel = parallel;
        this.threadCount = threadCount;
        this.booleanQuery = booleanQuery;
        this.useStatistics = useStatistics;
        this.isFirstRun = isFirstRun;
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

    public Integer getDbSize() {
        return dbSize;
    }

    public void setDbSize(Integer dbSize) {
        this.dbSize = dbSize;
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

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isBooleanQuery() {
        return booleanQuery;
    }

    public void setBooleanQuery(boolean booleanQuery) {
        this.booleanQuery = booleanQuery;
    }

    public void setDecompositionOptions(DecompositionOptions decompositionOptions) {
        this.decompositionOptions = decompositionOptions;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isFirstRun() {
        return isFirstRun;
    }

    public void setFirstRun(boolean firstRun) {
        isFirstRun = firstRun;
    }

    @Override
    public String toString() {
        return "BenchmarkConf{" +
                "db='" + db + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
