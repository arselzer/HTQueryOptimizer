package benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import org.apache.commons.cli.*;
import queryexecutor.QueryExecutor;
import queryexecutor.UnoptimizedQueryExecutor;
import queryexecutor.ViewQueryExecutor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class Benchmark {
    private static int DEFAULT_TIMEOUT = 25;
    private String dbRootDir;
    private String dbDir = null;
    private Properties connectionProperties;
    private int runs = 1;
    //private Connection conn;
    private String dbURL;
    // Use detkdecomp per default
    private Set<DecompositionOptions.DecompAlgorithm> decompAlgorithms
            = new HashSet<>(List.of(DecompositionOptions.DecompAlgorithm.DETKDECOMP));
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

    public void setDecompAlgorithms(List<DecompositionOptions.DecompAlgorithm> decompAlgos) {
        decompAlgorithms = new HashSet<>(decompAlgos);
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public static void main(String[] args) {
        Options options = new Options();
        Option setDb = new Option("d", "db", true, "test db");
        Option setTimeout = new Option("t", "timeout", true, "set query timeout");
        setTimeout.setType(Integer.class);
        Option setRuns = new Option("r", "runs", true, "set runs");
        Option setDecompositionAlgos = new Option("m", "methods", true, "set decomposition method");
        setRuns.setType(Integer.class);
        options.addOption(setDb);
        options.addOption(setTimeout);
        options.addOption(setRuns);
        options.addOption(setDecompositionAlgos);
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
            if (cmd.hasOption("runs")) {
                benchmark.setRuns(Integer.parseInt(cmd.getOptionValue("runs")));
            }
            if (cmd.hasOption("methods")) {
                String[] decompMethods = cmd.getOptionValue("methods").split(",");
                benchmark.setDecompAlgorithms(Arrays.stream(decompMethods).
                        map(DecompositionOptions.DecompAlgorithm::valueOf)
                        .collect(Collectors.toList()));
            }
            benchmark.run();

            File resultsDirectory = new File("benchmark-results-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
            resultsDirectory.mkdirs();

            SummarizedResultsCSVGenerator csvGenerator = new SummarizedResultsCSVGenerator();

            for (BenchmarkResult res : benchmark.getResults()) {
                csvGenerator.addResult(res);

                BenchmarkConf conf = res.getConf();

                File subResultsDir = new File(resultsDirectory.getAbsolutePath().toString() + "/" + conf.getDb() + "/" + conf.getQuery() + "-" + conf.getSuffix());
                subResultsDir.mkdirs();

                File hypergraphFile = new File(subResultsDir + "/hypergraph.dtl");

                // Write hypergraph
                PrintWriter hypergraphWriter = new PrintWriter(hypergraphFile);
                hypergraphWriter.write(res.getHypergraph().toDTL());
                hypergraphWriter.close();

                // Write graph rendering
                res.getHypergraph().toPDF(Paths.get(subResultsDir + "/hypergraph.pdf"));

                // Write out the java data structure
                File resultTxtFile = new File(subResultsDir + "/result.txt");
                PrintWriter resultStringWriter = new PrintWriter(resultTxtFile);
                resultStringWriter.write(res.toString());
                resultStringWriter.close();

                // Write out the original query
                File queryFile = new File(subResultsDir + "/query.sql");
                PrintWriter queryWriter = new PrintWriter(queryFile);
                queryWriter.write(res.getQuery());
                queryWriter.close();

                // Write out the optimized generated query
                File generatedQueryFile = new File(subResultsDir + "/generated.sql");
                PrintWriter generatedQueryWriter = new PrintWriter(generatedQueryFile);
                generatedQueryWriter.write(res.getGeneratedQuery());
                generatedQueryWriter.close();

                // Write out the serialized results as json
                File resultJsonFile = new File(subResultsDir + "/result.json");
                PrintWriter resultJsonWriter = new PrintWriter(resultJsonFile);
                resultJsonWriter.write(gson.toJson(res));
                resultJsonWriter.close();
            }

            Files.write(Paths.get(resultsDirectory + "/summary.csv"), csvGenerator.getCSV().getBytes());

        } catch (FileNotFoundException e) {
            System.out.printf("File not found exception: %s\n", e.getMessage());
        } catch (InterruptedException | IOException e) {
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

            // Run create.sh
            ProcessBuilder pb = new ProcessBuilder();

            pb.command(createFile.getAbsolutePath());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(pb.start().getInputStream()));

            // Wait for psql to finish otherwise tables might be missing
            List<String> psqlOutput = reader.lines().collect(Collectors.toList());

            QueryExecutor originalQE = null;
            ViewQueryExecutor optimizedQE = null;

            try {
                originalQE = new UnoptimizedQueryExecutor(conn);
                optimizedQE = new ViewQueryExecutor(conn);
            } catch (SQLException e) {
                throw e;
                // Rethrow exceptions occuring during setup
            }

            // Set timeouts if specified
            if (conf.getQueryTimeout() != null) {
                originalQE.setTimeout(conf.getQueryTimeout());
                optimizedQE.setTimeout(conf.getQueryTimeout());
            }

            optimizedQE.setDecompositionOptions(conf.getDecompositionOptions());

            /** Execute optimized query **/

            ResultSet optimizedRS = null;
            try {
                conn.prepareStatement("vacuum analyze;").execute();

                long startTimeOptimized = System.currentTimeMillis();
                optimizedRS = optimizedQE.execute(query);
                result.setOptimizedTotalRuntime(System.currentTimeMillis() - startTimeOptimized);
                result.setOptimizedQueryRuntime(optimizedQE.getQueryRunningTime());

                int optimizedCount = 0;
                if (conf.getSkipRows()) {
                    optimizedRS.last();
                    optimizedCount = optimizedRS.getRow();
                }
                else {
                    while (optimizedRS.next()) {
                        optimizedCount++;
                    }
                }

                result.setOptimizedRows(optimizedCount);
                ResultSetMetaData metaData = optimizedRS.getMetaData();
                result.setOptimizedColumns(metaData.getColumnCount());

                // Close the resultSet to close the PreparedStatement such that no memory is leaked
                optimizedRS.close();
            } catch (SQLException e) {
                System.err.println("Timeout: " + e.getMessage());
                result.setOptimizedQueryTimeout(true);
            } catch (TableNotFoundException e) {
                System.err.println("Table not found: " + e.getMessage());
            }

            result.setHypergraph(optimizedQE.getHypergraph());
            result.setJoinTree(optimizedQE.getJoinTree());
            result.setGeneratedQuery(optimizedQE.getGeneratedFunction());

            /** Execute original query **/

            ResultSet originalRS = null;
            try {
                conn.prepareStatement("vacuum analyze;").execute();
                long startTimeUnoptimized = System.currentTimeMillis();
                originalRS = originalQE.execute(query);
                result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

                int originalCount = 0;
                if (conf.getSkipRows()) {
                    originalRS.last();
                    originalCount = originalRS.getRow();
                }
                else {
                    while (originalRS.next()) {
                        originalCount++;
                    }
                }

                result.setUnoptimizedRows(originalCount);


                ResultSetMetaData metaData = originalRS.getMetaData();
                result.setUnoptimizedColumns(metaData.getColumnCount());

                originalRS.close();
            } catch (SQLException e) {
                System.err.println("Timeout: " + e.getMessage());
                result.setUnoptimizedQueryTimeout(true);
            } catch (TableNotFoundException e) {
                System.err.println("Table not found: " + e.getMessage());
            }

            results.add(result);
        }
    }

    private List<BenchmarkConf> generateBenchmarkConfigs() {
        LinkedList<BenchmarkConf> confs = new LinkedList<>();

        DecompositionOptions detkdecompOptions = new DecompositionOptions(DecompositionOptions.DecompAlgorithm.DETKDECOMP);
        DecompositionOptions balancedGoOptions = new DecompositionOptions(DecompositionOptions.DecompAlgorithm.BALANCEDGO);

        File dataRootDir = new File(dbRootDir);
        File[] subdirs = dataRootDir.listFiles(File::isDirectory);

        for (File subdir : subdirs) {
            String dbName = subdir.getName();

            if (dbDir == null || dbName.equals(dbDir)) {
                File[] sqlFiles = subdir.listFiles(file -> file.getName().endsWith(".sql") && !file.getName().equals("create.sql"));

                if (sqlFiles != null) {
                    for (File file : sqlFiles) {
                        for (int i = 1; i <= runs; i++) {
                            if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.DETKDECOMP)) {
                                confs.add(new BenchmarkConf(dbName, file.getName(), String.format("detkdecomp-%02d", i),
                                        detkdecompOptions, DEFAULT_TIMEOUT, i));
                            }
                            if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.BALANCEDGO)) {
                                confs.add(new BenchmarkConf(dbName, file.getName(), String.format("balancedgo-%02d", i),
                                        balancedGoOptions, DEFAULT_TIMEOUT, i));
                            }
                        }
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
