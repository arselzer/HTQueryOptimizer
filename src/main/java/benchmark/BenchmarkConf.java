package benchmark;

public class BenchmarkConf {
    private String db;
    private String query;

    public BenchmarkConf(String db, String query) {
        this.db = db;
        this.query = query;
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

    @Override
    public String toString() {
        return "BenchmarkConf{" +
                "db='" + db + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
