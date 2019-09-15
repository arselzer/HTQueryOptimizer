package benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.QueryConversionException;
import queryexecutor.QueryExecutor;
import queryexecutor.UnoptimizedQueryExecutor;
import queryexecutor.ViewQueryExecutor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
        System.out.printf("Benchmarking %s/%s", dbFileName, queryFileName);

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

        QueryExecutor uoqe = new UnoptimizedQueryExecutor(conn);
        ViewQueryExecutor qe = new ViewQueryExecutor(conn);

        conn.prepareStatement("vacuum analyze;").execute();
        long startTimeOptimized = System.currentTimeMillis();
        ResultSet rs = qe.execute(query);
        result.setOptimizedTotalRuntime(System.currentTimeMillis() - startTimeOptimized);
        result.setOptimizedQueryRuntime(qe.getQueryRunningTime());

        int count1 = 0;
        while (rs.next()) {
            count1++;
        }
        result.setOptimizedRows(count1);

        result.setHypergraph(qe.getHypergraph());
        result.setJoinTree(qe.getJoinTree());

        conn.prepareStatement("vacuum analyze;").execute();
        long startTimeUnoptimized = System.currentTimeMillis();
        ResultSet rs2 = uoqe.execute(query);
        result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

        int count2 = 0;
        while (rs2.next()) {
            count2++;
        }
        result.setUnoptimizedRows(count2);

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

            File[] sqlFiles = subdir.listFiles(file -> file.getName().endsWith(".sql") && !file.getName().equals("create.sql"));

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

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Connection conn = DriverManager.getConnection(url, properties);

            Benchmark benchmark = new Benchmark(System.getProperty("user.dir") + "/data", conn);
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

                File resultJsonFile = new File(resultDir + "/result.json");
                PrintWriter resultJsonWriter = new PrintWriter(resultJsonFile);
                resultJsonWriter.write(gson.toJson(res));
                resultJsonWriter.close();

                //System.out.println(res);
            }
        } catch (SQLException e) {
            System.out.printf("Error connecting to db: %s\n", e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.printf("File not found exception: %s\n", e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
