package benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.QueryConversionException;
import org.apache.commons.cli.*;
import queryexecutor.QueryExecutor;
import queryexecutor.UnoptimizedQueryExecutor;
import queryexecutor.ViewQueryExecutor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Benchmark {
    private static int DEFAULT_TIMEOUT = 10;
    private String dbRootDir;
    private String dbDir = null;
    private Properties connectionProperties;
    //private Connection conn;
    private String dbURL;
    private List<BenchmarkResult> results = new LinkedList<>();

    public Benchmark(String dbRootDir, Properties connectionProperties, String dbURL) {
        this.dbRootDir = dbRootDir;
        this.connectionProperties = connectionProperties;
        this.dbURL = dbURL;
    }

    public Benchmark(String dbRootDir, Properties connectionProperties, String dbURL, String db) {
        this.dbRootDir = dbRootDir;
        this.connectionProperties = connectionProperties;
        this.dbURL = dbURL;
        this.dbDir = db;
    }

    public static void main(String[] args) {
        Options options = new Options();
        Option setDb = new Option("d", "db", true, "test db");
        options.addOption(setDb);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        //properties.setProperty("ssl", "true");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Benchmark benchmark;
            if (cmd.hasOption("db")) {
                benchmark = new Benchmark(System.getProperty("user.dir") + "/data", properties, url, cmd.getOptionValue("db"));
            } else {
                benchmark = new Benchmark(System.getProperty("user.dir") + "/data", properties, url);
            }
            benchmark.run();

            File resultsDirectory = new File("benchmark-results-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
            resultsDirectory.mkdirs();

            for (BenchmarkResult res : benchmark.getResults()) {
                BenchmarkConf conf = res.getConf();

                File resultDir = new File(resultsDirectory.getAbsolutePath().toString() + "/" + conf.getDb() + "/" + conf.getQuery());
                resultDir.mkdirs();

                File hypergraphFile = new File(resultDir + "/hypergraph.dtl");

                // Write hypergraph
                PrintWriter hypergraphWriter = new PrintWriter(hypergraphFile);
                hypergraphWriter.write(res.getHypergraph().toDTL());
                hypergraphWriter.close();

                // Write graph rendering
                res.getHypergraph().toPDF(Paths.get(resultDir + "/hypergraph.pdf"));

                // Write out the java data structure
                File resultTxtFile = new File(resultDir + "/result.txt");
                PrintWriter resultStringWriter = new PrintWriter(resultTxtFile);
                resultStringWriter.write(res.toString());
                resultStringWriter.close();

                // Write out the original query
                File queryFile = new File(resultDir + "/query.sql");
                PrintWriter queryWriter = new PrintWriter(queryFile);
                queryWriter.write(res.getQuery());
                queryWriter.close();

                File generatedQueryFile = new File(resultDir + "/generated.sql");
                PrintWriter generatedQueryWriter = new PrintWriter(generatedQueryFile);
                generatedQueryWriter.write(res.getGeneratedQuery());
                generatedQueryWriter.close();

                File resultJsonFile = new File(resultDir + "/result.json");
                PrintWriter resultJsonWriter = new PrintWriter(resultJsonFile);
                resultJsonWriter.write(gson.toJson(res));
                resultJsonWriter.close();

                //System.out.println(res);
            }
        } catch (FileNotFoundException e) {
            System.out.printf("File not found exception: %s\n", e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void benchmark(BenchmarkConf conf) throws IOException, QueryConversionException, SQLException {
        String dbFileName = conf.getDb();
        String queryFileName = conf.getQuery();
        BenchmarkResult result = new BenchmarkResult(conf);
        System.out.printf("Benchmarking %s/%s\n", dbFileName, queryFileName);

        // Try with resources to close each connection. Otherwise memory leaks might occur
        try (Connection conn = DriverManager.getConnection(dbURL, connectionProperties)) {

            File createFile = new File(dbRootDir + "/" + dbFileName + "/create.sh");
            File queryFile = new File(dbRootDir + "/" + dbFileName + "/" + queryFileName);

            String query = Files.lines(queryFile.toPath()).collect(Collectors.joining("\n"));
            result.setQuery(query);

            ProcessBuilder pb = new ProcessBuilder();

            pb.command(createFile.getAbsolutePath());
            //pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(pb.start().getInputStream()));

            String output = "";
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
            //System.out.println(output);

            QueryExecutor uoqe = null;
            ViewQueryExecutor qe = null;
            try {
                uoqe = new UnoptimizedQueryExecutor(conn);
                qe = new ViewQueryExecutor(conn);
            } catch (SQLException e) {
                throw e;
                // Rethrow exceptions occuring during setup
            }

            // Set timeouts if specified
            if (conf.getQueryTimeout() != null) {
                uoqe.setTimeout(conf.getQueryTimeout());
                qe.setTimeout(conf.getQueryTimeout());
            }

            ResultSet rs = null;
            try {
                conn.prepareStatement("vacuum analyze;").execute();
                long startTimeOptimized = System.currentTimeMillis();
                rs = qe.execute(query);
                result.setOptimizedTotalRuntime(System.currentTimeMillis() - startTimeOptimized);
                result.setOptimizedQueryRuntime(qe.getQueryRunningTime());

                int count1 = 0;
                while (rs.next()) {
                    count1++;
                }
                result.setOptimizedRows(count1);
                rs.close();
            } catch (SQLException e) {
                System.err.println("Timeout: " + e.getMessage());
                result.setOptimizedQueryTimeout(true);
                // Close the resultSet to close the PreparedStatement such that no memory is leaked
            }

            result.setHypergraph(qe.getHypergraph());
            result.setJoinTree(qe.getJoinTree());
            result.setGeneratedQuery(qe.getGeneratedFunction());

            ResultSet rs2 = null;
            try {
                conn.prepareStatement("vacuum analyze;").execute();
                long startTimeUnoptimized = System.currentTimeMillis();
                rs2 = uoqe.execute(query);
                result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

                int count2 = 0;
                while (rs2.next()) {
                    count2++;
                }
                result.setUnoptimizedRows(count2);
                rs2.close();
            } catch (SQLException e) {
                System.err.println("Timeout: " + e.getMessage());
                result.setUnoptimizedQueryTimeout(true);
            }

            results.add(result);
        }
    }

    private List<BenchmarkConf> generateBenchmarkConfigs() {
        LinkedList<BenchmarkConf> confs = new LinkedList<>();

        File dir = new File(dbRootDir);
        File[] subdirs = dir.listFiles(File::isDirectory);

        for (File subdir : subdirs) {
            String dbName = subdir.getName();

            if (dbDir == null || dbName.equals(dbDir)) {
                File[] sqlFiles = subdir.listFiles(file -> file.getName().endsWith(".sql") && !file.getName().equals("create.sql"));

                if (sqlFiles != null) {
                    for (File file : sqlFiles) {
                        confs.add(new BenchmarkConf(dbName, file.getName(), DEFAULT_TIMEOUT));
                    }
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

                // Do garbage collection because otherwise the benchmark crashes due to OOM ...
                System.gc();
                System.runFinalization();
            } catch (SQLException | IOException | QueryConversionException e) {
                System.out.println("Error benchmarking: " + e.getMessage());
            }
        }
    }

    public List<BenchmarkResult> getResults() {
        return results;
    }
}
