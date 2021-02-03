package benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import hypergraph.WeightedHypergraph;
import org.apache.commons.cli.*;
import queryexecutor.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

public class Benchmark {
    private static int DEFAULT_TIMEOUT = 25;
    private int queryTimeout = DEFAULT_TIMEOUT;
    private String dbRootDir;
    private String dbDir = null;
    private Properties connectionProperties;
    private Integer threadCount = null;
    private int runs = 1;
    private String dbURL;
    // Use balancedgo per default
    private Set<DecompositionOptions.DecompAlgorithm> decompAlgorithms
            = new HashSet<>(List.of(DecompositionOptions.DecompAlgorithm.BALANCEDGO));
    private Set<String> queries = null;
    private List<BenchmarkResult> results = new LinkedList<>();
    private boolean checkCorrectness = false;
    private boolean runparallel = false;

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

        Option setDb = new Option("d", "db", true, "the database(s) to use, default: all");
        Option setTimeout = new Option("t", "timeout", true, "the timeout of queries in seconds, e.g. `-t 20`, default: 25s");
        setTimeout.setType(Integer.class);
        Option setRuns = new Option("r", "runs", true, "the number of repetitions of each run, default: 1");
        setRuns.setType(Integer.class);
        Option setDecompositionAlgos = new Option("m", "methods", true, "the algorithms used, e.g. `-a BALANCEDGO,DETKDECOMP`, default: BALANCEDGO");
        Option setQueries = new Option("q", "queries", true, "the queries to benchmark, default: all");
        Option setCheckCorrectness = new Option("c", "check", false, "check if the rows are equivalent in the original and optimized query i.e. each row occurs the same number of times\n" +
                "  Warning: currently does not work correctly for `select * from ...` queries");
        Option runParallel = new Option("p", "parallel", false, "execute the query in parallel");
        Option parallelThreads = new Option(null, "threads", true, "the number of threads used for parallel execution");
        parallelThreads.setType(Integer.class);

        options.addOption(setDb);
        options.addOption(setTimeout);
        options.addOption(setRuns);
        options.addOption(setDecompositionAlgos);
        options.addOption(setQueries);
        options.addOption(setCheckCorrectness);
        options.addOption(runParallel);
        options.addOption(parallelThreads);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments:" + e.getMessage());
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -cp {jar} \"benchmark.Benchmark\"", "", options, "", true);
            System.exit(1);
        }

        String url = "jdbc:postgresql://localhost/testdb";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

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
            if (cmd.hasOption("queries")) {
                String[] queries = cmd.getOptionValue("queries").split(",");
                benchmark.setQueries(Set.of(queries));
            }
            if (cmd.hasOption("timeout")) {
                benchmark.setQueryTimeout(Integer.parseInt(cmd.getOptionValue("timeout")));
            }
            if (cmd.hasOption("check")) {
                benchmark.setCheckCorrectness(true);
            }
            if (cmd.hasOption("parallel")) {
                benchmark.setRunparallel(true);
            }
            if (cmd.hasOption("threads")) {
                Integer threadCount = Integer.parseInt(cmd.getOptionValue("threads"));
                if (threadCount < 1) {
                    throw new IllegalArgumentException("The thread count has to be at least 1");
                }
                benchmark.setThreadCount(threadCount);
            }

            benchmark.run();

            File resultsDirectory = new File("benchmark-results-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date()));
            resultsDirectory.mkdirs();

            SummarizedResultsCSVGenerator csvGenerator = new SummarizedResultsCSVGenerator();

            for (BenchmarkResult res : benchmark.getResults()) {
                csvGenerator.addResult(res);

                BenchmarkConf conf = res.getConf();

                File subResultsDir = new File(resultsDirectory.getAbsolutePath() + "/" + conf.getDb() + "/" + conf.getQuery() + "-" + conf.getSuffix());
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

                if (res.getGeneratedQuery() != null) {
                    // Write out the optimized generated query
                    File generatedQueryFile = new File(subResultsDir + "/generated.sql");
                    PrintWriter generatedQueryWriter = new PrintWriter(generatedQueryFile);
                    generatedQueryWriter.write(res.getGeneratedQuery());
                    generatedQueryWriter.close();
                }

                // Write out the serialized results as json
                File resultJsonFile = new File(subResultsDir + "/result.json");
                PrintWriter resultJsonWriter = new PrintWriter(resultJsonFile);
                resultJsonWriter.write(gson.toJson(res));
                resultJsonWriter.close();

                if (res.getHypergraph() instanceof WeightedHypergraph) {
                    File hgWeightsFile = new File(subResultsDir + "/hg-weights.csv");
                    PrintWriter weightsWriter = new PrintWriter(hgWeightsFile);
                    weightsWriter.write(((WeightedHypergraph) res.getHypergraph()).toWeightsFile());
                    weightsWriter.close();
                }
            }

            Files.write(Paths.get(resultsDirectory + "/summary.csv"), csvGenerator.getCSV().getBytes());

        } catch (FileNotFoundException e) {
            System.out.printf("File not found exception: %s\n", e.getMessage());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void setDecompAlgorithms(List<DecompositionOptions.DecompAlgorithm> decompAlgos) {
        decompAlgorithms = new HashSet<>(decompAlgos);
    }

    public void setQueries(Set<String> queries) {
        this.queries = queries;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public void setQueryTimeout(int timeout) {
        this.queryTimeout = timeout;
    }

    public void setCheckCorrectness(boolean checkCorrectness) {
        this.checkCorrectness = checkCorrectness;
    }

    public void setRunparallel(boolean runparallel) {
        this.runparallel = runparallel;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    private void benchmark(BenchmarkConf conf) throws IOException, QueryConversionException, SQLException {
        String dbFileName = conf.getDb();
        String queryFileName = conf.getQuery();
        BenchmarkResult result = new BenchmarkResult(conf);
        System.out.printf("Benchmarking %s/%s (size %s, run %s)\n",
                dbFileName, queryFileName, conf.getDbSize(), conf.getRun());

        ConnectionPool connPool;
        if (conf.getThreadCount() == null) {
            connPool = new ConnectionPool(dbURL, connectionProperties);
        }
        else {
            connPool = new ConnectionPool(dbURL, connectionProperties, conf.getThreadCount());
        }
        File createFile = new File(dbRootDir + "/" + dbFileName + "/create.sh");
        File queryFile = new File(dbRootDir + "/" + dbFileName + "/" + queryFileName);

        String query = Files.lines(queryFile.toPath()).collect(Collectors.joining("\n"));
        result.setQuery(query);

        // Run create.sh
        ProcessBuilder pb = new ProcessBuilder();

        pb.command(createFile.getAbsolutePath(), String.format("%d", conf.getDbSize()));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(pb.start().getInputStream()));

        // Wait for psql to finish otherwise tables might be missing
        List<String> psqlOutput = reader.lines().collect(Collectors.toList());
        System.out.println(String.join(" ", psqlOutput));

        QueryExecutor originalQE = null;
        TempTableQueryExecutor optimizedQE = null;

        Connection conn = DriverManager.getConnection(dbURL, connectionProperties);

        try {
            originalQE = new UnoptimizedQueryExecutor(conn);

            if (conf.isParallel()) {
                optimizedQE = new ParallelTempTableQueryExecutor(connPool);
            }
            else {
                optimizedQE = new TempTableQueryExecutor(connPool);
            }
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

        HashMap<String, Integer> optimizedRowCount = new HashMap<>();

        ResultSet optimizedRS = null;
        try {
            conn.prepareStatement("vacuum analyze;").execute();

            long startTimeOptimized = System.currentTimeMillis();
            optimizedRS = optimizedQE.execute(query);
            result.setOptimizedTotalRuntime(System.currentTimeMillis() - startTimeOptimized);
            result.setOptimizedQueryRuntime(optimizedQE.getQueryRunningTime());

            ResultSetMetaData metaData = optimizedRS.getMetaData();
            int colCount = metaData.getColumnCount();
            result.setOptimizedColumns(colCount);

            int optimizedCount = 0;
            if (!checkCorrectness) {
                optimizedRS.last();
                optimizedCount = optimizedRS.getRow();
            } else {
                while (optimizedRS.next()) {
                    String row = "";
                    for (int i = 1; i <= colCount; i++) {
                        row += optimizedRS.getString(i) + ",";
                    }
                    optimizedRowCount.putIfAbsent(row, 1);
                    optimizedRowCount.computeIfPresent(row, (__, cnt) -> cnt + 1);

                    optimizedCount++;
                }
            }

            result.setOptimizedRows(optimizedCount);

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

        HashMap<String, Integer> originalRowCount = new HashMap<>();
        ResultSet originalRS = null;
        try {
            conn.prepareStatement("vacuum analyze;").execute();
            long startTimeUnoptimized = System.currentTimeMillis();
            originalRS = originalQE.execute(query);
            result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

            ResultSetMetaData metaData = originalRS.getMetaData();
            int colCount = metaData.getColumnCount();
            result.setUnoptimizedColumns(colCount);

            int originalCount = 0;
            if (!checkCorrectness) {
                originalRS.last();
                originalCount = originalRS.getRow();
            } else {
                while (originalRS.next()) {
                    String row = "";
                    for (int i = 1; i <= colCount; i++) {
                        row += originalRS.getString(i) + ",";
                    }
                    originalRowCount.putIfAbsent(row, 1);
                    originalRowCount.computeIfPresent(row, (__, cnt) -> cnt + 1);

                    originalCount++;
                }
            }

            result.setUnoptimizedRows(originalCount);

            originalRS.close();
        } catch (SQLException e) {
            System.err.println("Timeout: " + e.getMessage());
            result.setUnoptimizedQueryTimeout(true);
        } catch (TableNotFoundException e) {
            System.err.println("Table not found: " + e.getMessage());
        }

        // Check if all rows occur the same number of times
        boolean correctResult = true;

        for (String key : optimizedRowCount.keySet()) {
            Integer optCount = optimizedRowCount.get(key);
            Integer origCount = originalRowCount.get(key);
            if (!optCount.equals(origCount)) {
                correctResult = false;
                System.err.printf("Row counts of row %s do not match: %s (orig) vs %s (opt)\n", key, origCount, optCount);
            }
        }

        conn.close();

        result.setOptimizedResultCorrect(correctResult);

        results.add(result);
    }

    private List<BenchmarkConf> generateBenchmarkConfigs() throws IOException {
        LinkedList<BenchmarkConf> confs = new LinkedList<>();

        DecompositionOptions detkdecompOptions = new DecompositionOptions(DecompositionOptions.DecompAlgorithm.DETKDECOMP);
        DecompositionOptions balancedGoOptions = new DecompositionOptions(DecompositionOptions.DecompAlgorithm.BALANCEDGO);

        File dataRootDir = new File(dbRootDir);
        File[] subdirs = dataRootDir.listFiles(File::isDirectory);

        for (File subdir : subdirs) {
            String dbName = subdir.getName();

            int defaultMinDBSize = 1;
            int defaultMaxDBSize = 1;
            Map<String, DBGenConfig> dbGenSizeMap = new HashMap<>();

            File confFile = new File(dbRootDir + "/" + dbName + "/config.json");
            if (confFile.exists()) {
                Gson gson = new Gson();
                String confJSONString = Files.lines(confFile.toPath()).collect(Collectors.joining(""));

                Type dbGenMapType = (new TypeToken<Map<String, DBGenConfig>>() {
                }).getType();

                dbGenSizeMap = gson.fromJson(confJSONString, dbGenMapType);

                // Set the default size (if specified), otherwise it is 1
                if (dbGenSizeMap.containsKey("*")) {
                    defaultMinDBSize = dbGenSizeMap.get("*").getDbSizeMin();
                    defaultMaxDBSize = dbGenSizeMap.get("*").getDbSizeMax();
                }
            }

            if (dbDir == null || dbName.equals(dbDir)) {
                File[] sqlFiles = subdir.listFiles(file -> file.getName().endsWith(".sql") && !file.getName().equals("create.sql"));

                if (sqlFiles != null) {
                    for (File file : sqlFiles) {
                        String fileName = file.getName();

                        if (queries == null || queries.contains(fileName)) {
                            int minDBSize = defaultMinDBSize;
                            int maxDBSize = defaultMaxDBSize;

                            // Set the specific db size for the query if specified
                            if (dbGenSizeMap.containsKey(fileName)) {
                                DBGenConfig dbGenConfig = dbGenSizeMap.get(fileName);
                                minDBSize = dbGenConfig.getDbSizeMin();
                                maxDBSize = dbGenConfig.getDbSizeMax();
                            }

                            for (int size = minDBSize; size <= maxDBSize; size++) {
                                for (int run = 1; run <= runs; run++) {
                                    if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.DETKDECOMP)) {
                                        confs.add(new BenchmarkConf(dbName, file.getName(), String.format("detkdecomp-%02d-%02d", size, run),
                                                detkdecompOptions, queryTimeout, run, size, runparallel, threadCount));
                                    }
                                    if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.BALANCEDGO)) {
                                        confs.add(new BenchmarkConf(dbName, file.getName(), String.format("balancedgo-%02d-%02d", size, run),
                                                balancedGoOptions, queryTimeout, run, size, runparallel, threadCount));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return confs;
    }

    public void run() {
        try {
            List<BenchmarkConf> confs = generateBenchmarkConfigs();

            for (BenchmarkConf conf : confs) {
                benchmark(conf);

                // Do garbage collection because otherwise the benchmark crashes due to OOM ...
                System.gc();
                System.runFinalization();
            }
        } catch (SQLException | IOException | QueryConversionException e) {
            System.out.println("Error benchmarking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<BenchmarkResult> getResults() {
        return results;
    }
}
