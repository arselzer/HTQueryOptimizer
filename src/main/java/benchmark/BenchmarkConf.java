package benchmark;

public class BenchmarkConf {
    private String db;
    private String query;
    // Timeout in seconds
    private Integer queryTimeout = null;

    public BenchmarkConf(String db, String query) {
        this.db = db;
        this.query = query;
    }

    public BenchmarkConf(String db, String query, Integer timeout) {
        this.db = db;
        this.query = query;
        this.queryTimeout = timeout;
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

    @Override
    public String toString() {
        return "BenchmarkConf{" +
                "db='" + db + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
