package benchmark;

import exceptions.QueryConversionException;
import queryexecutor.QueryExecutor;
import queryexecutor.UnoptimizedQueryExecutor;
import queryexecutor.ViewQueryExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Benchmark {
    private String dbRootDir;
    private Connection conn;

    private List<BenchmarkResult> results = new LinkedList<>();

    public Benchmark(String dbRootDir, Connection conn) {
        this.dbRootDir = dbRootDir;
        this.conn = conn;
    }

    private void benchmark(BenchmarkConf conf) throws SQLException, IOException, QueryConversionException {
        String dbFileName = conf.getDb();
        String queryFileName = conf.getQuery();
        BenchmarkResult result = new BenchmarkResult(conf);

        File createFile = new File(dbRootDir + "/" + dbFileName + "/create.sh");
        File queryFile = new File(dbRootDir + "/" + dbFileName + "/" + queryFileName);

        String query = Files.lines(queryFile.toPath()).collect(Collectors.joining("\n"));

        ProcessBuilder pb = new ProcessBuilder();

        pb.command(createFile.getAbsolutePath());


        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        ViewQueryExecutor qe = new ViewQueryExecutor(conn);

        conn.prepareStatement("vacuum analyze;").execute();
        long startTimeOptimized = System.currentTimeMillis();
        ResultSet rs = qe.execute(query);
        result.setOptimizedRuntime(System.currentTimeMillis() - startTimeOptimized);

        conn.prepareStatement("vacuum analyze;").execute();
        long startTimeUnoptimized = System.currentTimeMillis();
        uoqe.execute(query);
        result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

        results.add(result);

//        int i = 0;
//        while (rs.next() && i < 100) {
//            System.out.println(rs.getString(1));
//            i++;
//        }

    }

    private List<BenchmarkConf> generateBenchmarkConfigs() {
        LinkedList<BenchmarkConf> confs = new LinkedList<>();

        File dir = new File(dbRootDir);
        File[] subdirs = dir.listFiles(File::isDirectory);

        for (File subdir : subdirs) {
            String dbName = subdir.getName();

            File[] sqlFiles = subdir.listFiles(file -> file.getName().endsWith(".sql"));

            if (sqlFiles != null) {
                for (File file: sqlFiles) {
                    confs.add(new BenchmarkConf(dbName, file.getName()));
                }
            }
        }

        return confs;
    }

    public void run() {
        List<BenchmarkConf> confs = generateBenchmarkConfigs();

        for (BenchmarkConf conf : confs) {
            try {
                benchmark(conf);
            } catch (SQLException | IOException | QueryConversionException e) {
                System.out.println("Error benchmarking: " + e.getMessage());
            }
        }
    }

    public List<BenchmarkResult> getResults() {
        return results;
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        //properties.setProperty("ssl", "true");

        try {
            Connection conn = DriverManager.getConnection(url, properties);

            Benchmark benchmark = new Benchmark(System.getProperty("user.dir") + "/data", conn);
            benchmark.run();

            for (BenchmarkResult res : benchmark.getResults()) {
                System.out.println(res);
            }
        } catch (SQLException e) {
            System.out.printf("Error connecting to db: %s\n", e.getMessage());
        }


    }
}
