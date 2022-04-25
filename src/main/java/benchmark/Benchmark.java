package benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import hypergraph.WeightedHypergraph;
import org.apache.commons.cli.*;
import query.ParallelQueryExecution;
import query.SQLQuery;
import queryexecutor.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

public class Benchmark {
    private static Options options = new Options();
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
    private boolean booleanQuery = false;
    private boolean useStatistics = true;
    private boolean keepTables = false;
    private boolean analyzeQuery = false;
    private boolean insertData = true;
    private boolean noReinsert = false;
    private boolean dropTables = true;
    private boolean createIndexes = false;
    private boolean depthOpt = false;
    private boolean acyclicOpt = true;
    private boolean applyAggregates = true;
    private String tablespace = null;
    private boolean enumerateJoinTrees = false;

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

    public static void showHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -cp {jar} \"benchmark.Benchmark\"", "", options, "", true);
    }

    public static void main(String[] args) throws IOException {
        Option help = new Option("h", "help", false, "show this");
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
        Option booleanQuery = new Option(null, "boolean", false, "run the queries as boolean queries (checking whether there is a result only)");
        Option unweighted = new Option(null, "unweighted", false, "use statistics (i.e. weighted hypergraphs) for query optimization");
        Option keepTables = new Option(null, "keep-tables", false, "don't drop the tables and insert new data");
        Option analyzeQuery = new Option(null, "analyze", false, "analyze query");
        Option noCreate = new Option(null, "no-create", false, "don't insert data");
        Option noReinsert = new Option(null, "no-reinsert", false, "don't re-insert the data after the first run");
        Option noDrop = new Option(null, "no-drop", false, "don't drop temporary tables");
        Option useIndexes = new Option(null, "create-indexes", false, "create indexes on the temporary tables");
        Option depthOpt = new Option(null, "depth-opt", false, "the maximum depth of the generated join trees for acyclic queries");
        Option disableAcyclicTreeOpt = new Option(null, "no-acyclic-opt", false, "disable the join tree optimization and use BalancedGo");
        Option schemaFile = new Option(null, "schema-file", true, "the schema file to use for parsing the query");
        schemaFile.setType(String.class);
        Option tablespace = new Option(null, "tablespace", true, "the tablespace to use for generating the temporary tables");
        tablespace.setType(String.class);
        Option enumerate = new Option(null, "enumerate", false, "enumerate join trees");

        options.addOption(help);
        options.addOption(setDb);
        options.addOption(setTimeout);
        options.addOption(setRuns);
        options.addOption(setDecompositionAlgos);
        options.addOption(setQueries);
        options.addOption(setCheckCorrectness);
        options.addOption(runParallel);
        options.addOption(parallelThreads);
        options.addOption(booleanQuery);
        options.addOption(unweighted);
        options.addOption(keepTables);
        options.addOption(analyzeQuery);
        options.addOption(noCreate);
        options.addOption(noReinsert);
        options.addOption(noDrop);
        options.addOption(useIndexes);
        options.addOption(depthOpt);
        options.addOption(disableAcyclicTreeOpt);
        options.addOption(schemaFile);
        options.addOption(tablespace);
        options.addOption(enumerate);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments:" + e.getMessage());
            showHelp();
            System.exit(1);
        }

        if (cmd.hasOption("help")) {
            showHelp();
            System.exit(0);
        }

        String url = "jdbc:postgresql://localhost/testdb?loggerLevel=TRACE&loggerFile=pgjdbc.log";
        String user = "test";
        String password = "test";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        if (cmd.hasOption("boolean")) {
            benchmark.setBooleanQuery(true);
        }
        if (cmd.hasOption("unweighted")) {
            benchmark.setUseStatistics(false);
        }
        if (cmd.hasOption("keep-tables")) {
            benchmark.setKeepTables(true);
        }
        if (cmd.hasOption("analyze")) {
            benchmark.setAnalyzeQuery(true);
        }
        if (cmd.hasOption("no-create")) {
            benchmark.setInsertData(false);
        }
        if (cmd.hasOption("no-reinsert")) {
            benchmark.setNoReinsert(true);
        }
        if (cmd.hasOption("no-drop")) {
            benchmark.setDropTables(false);
        }
        if (cmd.hasOption("create-indexes")) {
            benchmark.setCreateIndexes(true);
        }
        if (cmd.hasOption("depth-opt")) {
            benchmark.setDepthOpt(true);
        }
        if (cmd.hasOption("no-acyclic-opt")) {
            benchmark.setAcyclicOpt(false);
        }
        if (cmd.hasOption("tablespace")) {
            benchmark.setTablespace(cmd.getOptionValue("tablespace"));
        }
        if (cmd.hasOption("enumerate")) {
            benchmark.setEnumerateJoinTrees(true);
        }

        String resultsDirectoryName = "benchmark-results-" + new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
        File resultsDirectory = new File(resultsDirectoryName);
        resultsDirectory.mkdirs();
        File latestDirectory = new File("latest-results");
        // Delete old "latest" results
        if (latestDirectory.exists()) {
            Files.walkFileTree(latestDirectory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        latestDirectory.mkdirs();

        Path link = Paths.get("latest-results/" + resultsDirectoryName);
        Files.createSymbolicLink(link, resultsDirectory.toPath().toAbsolutePath());

        SummarizedResultsCSVGenerator csvGenerator = new SummarizedResultsCSVGenerator();

        benchmark.run(resultsDirectory, csvGenerator);
    }

    private void dropAllTables(Connection conn) throws SQLException {
        // Taken from: https://stackoverflow.com/questions/3327312/how-can-i-drop-all-the-tables-in-a-postgresql-database
        System.out.println("Dropping all tables");
        conn.prepareStatement("DO $$ DECLARE\n" +
                "    r RECORD;\n" +
                "BEGIN\n" +
                "    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP\n" +
                "        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';\n" +
                "    END LOOP;\n" +
                "END $$;").execute();
    }

    private void insertData(BenchmarkConf conf) throws IOException {
        String dbFileName = conf.getDb();
        File createFile = new File(dbRootDir + "/" + dbFileName + "/create.sh");

        // Run create.sh
        ProcessBuilder pb = new ProcessBuilder();

        pb.command(createFile.getAbsolutePath(), String.format("%d", conf.getDbSize()));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(pb.start().getInputStream()));

        // Wait for psql to finish otherwise tables might be missing
        List<String> psqlOutput = reader.lines().collect(Collectors.toList());
        System.out.println("Running insert script: " + String.join(" ", psqlOutput));
    }

    private List<BenchmarkResult> enumerateBenchmark(BenchmarkConf conf) throws IOException, QueryConversionException, SQLException, TableNotFoundException {
        ConnectionPool connPool;
        if (conf.getThreadCount() == null) {
            connPool = new ConnectionPool(dbURL, connectionProperties);
        } else {
            connPool = new ConnectionPool(dbURL, connectionProperties, conf.getThreadCount());
        }

        ParallelTempTableQueryExecutor optimizedQE = null;
        try {
            optimizedQE = new ParallelTempTableQueryExecutor(connPool, useStatistics, true,
                    dropTables, createIndexes, depthOpt, applyAggregates);
        } catch (SQLException e) {
            throw e;
            // Rethrow exceptions occurring during setup
        }

        String dbFileName = conf.getDb();
        String queryFileName = conf.getQuery();
        File queryFile = new File(dbRootDir + "/" + dbFileName + "/" + queryFileName);
        String query = Files.lines(queryFile.toPath()).collect(Collectors.joining("\n"));

        List<BenchmarkResult> benchmarkResults = new LinkedList<>();
        List<ParallelQueryExecution> queryExecutions = optimizedQE.listQueryExecutions(query);

        System.out.println("enumerating join trees" + queryExecutions);
        int joinTreeNo = 1;
        for (ParallelQueryExecution queryExecution : queryExecutions) {
            BenchmarkResult result = benchmark(optimizedQE, queryExecution, conf);
            result.setJoinTreeNo(joinTreeNo++);
            benchmarkResults.add(result);
        }

        connPool.close();

        return benchmarkResults;
    }

    private BenchmarkResult benchmark(BenchmarkConf conf) throws IOException, QueryConversionException, SQLException {
        return benchmark(null, null, conf);
    }

    private BenchmarkResult benchmark(ParallelTempTableQueryExecutor queryExecutor, ParallelQueryExecution queryExecution, BenchmarkConf conf) throws IOException, QueryConversionException, SQLException {
        String dbFileName = conf.getDb();
        String queryFileName = conf.getQuery();
        BenchmarkResult result = new BenchmarkResult(conf);
        System.out.printf("Benchmarking %s/%s (size %s, run %s)\n",
                dbFileName, queryFileName, conf.getDbSize(), conf.getRun());

        ConnectionPool connPool;
        if (conf.getThreadCount() == null) {
            connPool = new ConnectionPool(dbURL, connectionProperties);
        } else {
            connPool = new ConnectionPool(dbURL, connectionProperties, conf.getThreadCount());
        }
        File queryFile = new File(dbRootDir + "/" + dbFileName + "/" + queryFileName);

        String query = Files.lines(queryFile.toPath()).collect(Collectors.joining("\n"));
        result.setQuery(query);

        Connection conn = DriverManager.getConnection(dbURL, connectionProperties);

        if (!(noReinsert && !conf.isFirstRun())) {
            if (!keepTables) {
                dropAllTables(conn);
            }

            if (insertData) {
                insertData(conf);
            }
        }

        UnoptimizedQueryExecutor originalQE = null;
        ParallelTempTableQueryExecutor optimizedQE = null;

        try {
            originalQE = new UnoptimizedQueryExecutor(conn);

            if (queryExecutor != null) {
                optimizedQE = queryExecutor;
            }
            else {
                optimizedQE = new ParallelTempTableQueryExecutor(connPool, useStatistics, true,
                        dropTables, createIndexes, depthOpt, applyAggregates);
            }
        } catch (SQLException e) {
            throw e;
            // Rethrow exceptions occurring during setup
        }

        // Set timeouts if specified
        if (conf.getQueryTimeout() != null) {
            originalQE.setTimeout(conf.getQueryTimeout());
            optimizedQE.setTimeout(conf.getQueryTimeout());
        }

        optimizedQE.setDecompositionOptions(conf.getDecompositionOptions());
        optimizedQE.setTablespace(tablespace);

        /** Execute optimized query **/

        System.gc();
        System.runFinalization();

        HashMap<String, Integer> optimizedRowCount = new HashMap<>();

        result.setStageRuntimes(new long[] {-1,-1,-1,-1});

        StatisticsResultSet optimizedRSWithStatistics = null;
        ResultSet optimizedRS = null;
        try {
            conn.prepareStatement("vacuum analyze;").execute();

            long startTimeOptimized = System.currentTimeMillis();
            if (!analyzeQuery) {
                if (conf.isBooleanQuery()) {
                    optimizedRSWithStatistics = optimizedQE.executeWithStatistics(query, true);
                } else {
                    if (queryExecution == null) {
                        optimizedRSWithStatistics = optimizedQE
                                .executeWithStatistics(query, false);
                    }
                    else {
                        optimizedRSWithStatistics = optimizedQE.executeWithStatistics(queryExecution, false, false);
                    }
                }
            }
            else {
                if (conf.isBooleanQuery()) {
                    optimizedRSWithStatistics = optimizedQE
                            .executeWithStatistics(query, true, true);
                } else {
                    if (queryExecution == null) {
                        optimizedRSWithStatistics = optimizedQE
                                .executeWithStatistics(query, false, true);
                    }
                    else {
                        optimizedRSWithStatistics = optimizedQE.executeWithStatistics(queryExecution, false, true);
                    }
                }

                result.setExecutionStatistics(optimizedRSWithStatistics.getStatistics());

                List<AnalyzeExecutionStatistics> analyzeExecutionStatistics = new LinkedList<>();
                for (ExecutionStatistics executionStatistics : optimizedRSWithStatistics.getStatistics()) {
                    analyzeExecutionStatistics.add((AnalyzeExecutionStatistics) executionStatistics);
                }
            }
            result.setStageRuntimes(optimizedQE.getStageRuntimes().clone());
            result.setOptimizedTotalRuntime(System.currentTimeMillis() - startTimeOptimized);
            result.setOptimizedQueryRuntime(optimizedQE.getQueryRunningTime());
            result.setDropTime(optimizedQE.getDropTime());
            result.setTotalPreprocessingTime(optimizedQE.getTotalPreprocessingTime());
            result.setHypergraphComputationRuntime(optimizedQE.getQuery().getHypergraphGenerationRuntime());
            result.setJoinTreeComputationRuntime(optimizedQE.getQuery().getJoinTreeGenerationRuntime());
            result.setExecutionStatistics(optimizedRSWithStatistics.getStatistics());
            result.setQueryExecution(queryExecution);

            optimizedRS = optimizedRSWithStatistics.getResultSet();

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
            System.err.println("Timeout or error: " + e.getMessage());
            result.setOptimizedQueryTimeout(true);
        } catch (TableNotFoundException e) {
            System.err.println("Table not found: " + e.getMessage());
        }

        result.setHypergraph(queryExecution.getHypergraph());
        result.setJoinTree(queryExecution.getJoinTree());
        result.setGeneratedQuery(optimizedQE.getGeneratedFunction());

        /** Execute original (unoptimized) query **/

        if (!noReinsert) {
            if (!keepTables) {
                dropAllTables(conn);
            }

            if (insertData) {
                insertData(conf);
            }
        }

        System.gc();
        System.runFinalization();

        HashMap<String, Integer> originalRowCount = new HashMap<>();
        ResultSet originalRS = null;
        try {
            conn.prepareStatement("vacuum analyze;").execute();
            long startTimeUnoptimized = System.currentTimeMillis();
            if (conf.isBooleanQuery()) {
                originalRS = originalQE.executeBoolean(query, analyzeQuery);
            }
            else {
                originalRS = originalQE.execute(query, analyzeQuery);
            }
            result.setUnoptimizedRuntime(System.currentTimeMillis() - startTimeUnoptimized);

            if (!analyzeQuery) {
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
            }
            else {
                String analyzeJSON = "";

                while (originalRS.next()) {
                    analyzeJSON += originalRS.getString(1);
                }

                result.setAnalyzeJSON(analyzeJSON);
            }

            originalRS.close();
        } catch (SQLException e) {
            System.err.println("Timeout or error: " + e.getMessage());
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

        return result;
    }

    private List<BenchmarkConf> generateBenchmarkConfigs() throws IOException {
        LinkedList<BenchmarkConf> confs = new LinkedList<>();

        DecompositionOptions detkdecompOptions = new DecompositionOptions(
                DecompositionOptions.DecompAlgorithm.DETKDECOMP, depthOpt, acyclicOpt);
        DecompositionOptions balancedGoOptions = new DecompositionOptions(
                DecompositionOptions.DecompAlgorithm.BALANCEDGO, depthOpt, acyclicOpt);

        File dataRootDir = new File(dbRootDir);
        File[] subdirs = dataRootDir.listFiles(File::isDirectory);

        for (File subdir : subdirs) {
            String dbName = subdir.getName();

            int defaultMinDBSize = 1;
            int defaultMaxDBSize = 1;
            int defaultSizeStep = 1;
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
                    defaultSizeStep = dbGenSizeMap.get("*").getStep();
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
                            int sizeStep = defaultSizeStep;

                            // Set the specific db size for the query if specified
                            if (dbGenSizeMap.containsKey(fileName)) {
                                DBGenConfig dbGenConfig = dbGenSizeMap.get(fileName);
                                minDBSize = dbGenConfig.getDbSizeMin();
                                maxDBSize = dbGenConfig.getDbSizeMax();
                                sizeStep = dbGenConfig.getStep();
                            }

                            for (int size = minDBSize; size <= maxDBSize; size += sizeStep) {
                                for (int run = 1; run <= runs; run++) {
                                    if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.DETKDECOMP)) {
                                        confs.add(new BenchmarkConf(dbName, file.getName(), String.format("detkdecomp-%02d-%02d", size, run),
                                                detkdecompOptions, queryTimeout, run, size, runparallel, threadCount, booleanQuery, useStatistics,
                                                run == 1, 1));
                                    }
                                    if (decompAlgorithms.contains(DecompositionOptions.DecompAlgorithm.BALANCEDGO)) {
                                        confs.add(new BenchmarkConf(dbName, file.getName(), String.format("balancedgo-%02d-%02d", size, run),
                                                balancedGoOptions, queryTimeout, run, size, runparallel, threadCount, booleanQuery, useStatistics,
                                                run == 1, 1));
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

    public void saveBenchmarkData(BenchmarkConf conf, BenchmarkResult res, File resultsDirectory, SummarizedResultsCSVGenerator csvGenerator) throws IOException, InterruptedException {
        csvGenerator.addResult(res);

        File subResultsDir = new File(resultsDirectory.getAbsolutePath() + "/" + conf.getDb() + "/" + conf.getQuery() + "-" + conf.getSuffix());
        subResultsDir.mkdirs();

        File hypergraphFile = new File(subResultsDir + "/hypergraph.dtl");

        // Write hypergraph
        if (res.getHypergraph() != null) {
            PrintWriter hypergraphWriter = new PrintWriter(hypergraphFile);
            hypergraphWriter.write(res.getHypergraph().toDTL());
            hypergraphWriter.close();

            // Write graph rendering
            res.getHypergraph().toPDF(Paths.get(subResultsDir + "/hypergraph.pdf"));
        }

        // Write out the java data structure
        File resultTxtFile = new File(subResultsDir + "/result.txt");
        PrintWriter resultStringWriter = new PrintWriter(resultTxtFile);
        resultStringWriter.write(res.toString());
        resultStringWriter.close();

        File jointreeFile = new File(subResultsDir + "/jointree.txt");
        PrintWriter jointreeStringWriter = new PrintWriter(jointreeFile);
        jointreeStringWriter.write(res.getJoinTree().toString());
        jointreeStringWriter.close();

        // Write out the original query
        File queryFile = new File(subResultsDir + "/original-query.sql");
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

        File executionFile = new File(subResultsDir + "/query-execution.sql");
        PrintWriter executionWriter = new PrintWriter(executionFile);
        String queryExecutionString = "";
        for (List<List<String>> stage : res.getQueryExecution().getSqlStatements()) {
            for (List<String> layer : stage) {
                for (String query : layer) {
                    queryExecutionString += query + "\n";
                }
            }
        }
        executionWriter.write(queryExecutionString);
        executionWriter.close();

        // Write out the serialized results as json
        File resultJsonFile = new File(subResultsDir + "/result.json");
        PrintWriter resultJsonWriter = new PrintWriter(resultJsonFile);
        //resultJsonWriter.write(gson.toJson(res));
        resultJsonWriter.close();

        if (res.getHypergraph() instanceof WeightedHypergraph) {
            File hgWeightsFile = new File(subResultsDir + "/hg-weights.csv");
            PrintWriter weightsWriter = new PrintWriter(hgWeightsFile);
            weightsWriter.write(((WeightedHypergraph) res.getHypergraph()).toWeightsFile());
            weightsWriter.close();
        }

        if (analyzeQuery) {
            File analyzeJSONFile = new File(subResultsDir + "/analyze.json");
            PrintWriter analyzeJsonWriter = new PrintWriter(analyzeJSONFile);
            analyzeJsonWriter.write(res.getAnalyzeJSON());
            analyzeJsonWriter.close();

            for (ExecutionStatistics executionStatistics : res.getExecutionStatistics()) {
                AnalyzeExecutionStatistics analyzeExecutionStatistics = (AnalyzeExecutionStatistics) executionStatistics;

                File stageDir = new File(subResultsDir + "/stage-" + executionStatistics.getQueryName());
                stageDir.mkdirs();

                int i = 1;
                System.out.println(analyzeExecutionStatistics.getAnalyzeJSONs());
                for (String analyzeJSON : analyzeExecutionStatistics.getAnalyzeJSONs()) {

                    File analyzeOptimizedJSONFile = new File(stageDir+ "/analyze-" + i + ".json");
                    PrintWriter analyzeOptimizedJsonWriter = new PrintWriter(analyzeOptimizedJSONFile);
                    analyzeOptimizedJsonWriter.write(analyzeJSON);
                    analyzeOptimizedJsonWriter.close();

                    File analyzeOptimizedQueryStringFile = new File(stageDir+ "/analyze-" + i + ".sql");
                    PrintWriter analyzeOptimizedQueryStringWriter = new PrintWriter(analyzeOptimizedQueryStringFile);
                    analyzeOptimizedQueryStringWriter.write(analyzeExecutionStatistics.getQueryStrings().get(i-1));
                    analyzeOptimizedQueryStringWriter.close();
                    i++;
                }
            }
        }

        String runtimeStatistics = "name,runtime,rowcounts,queries-rows\n";

        if (res.getExecutionStatistics() != null) {
            for (ExecutionStatistics stats : res.getExecutionStatistics()) {
                runtimeStatistics += stats.getQueryName() + "," + stats.getRuntime() + "," +
                                stats.getQueryRows().values().stream().map(i -> i.toString()).collect(Collectors.joining(";")) + "," +
                                stats.getQueryStrings().stream()
                                .map(queryStr -> "\"" + queryStr.replace("\n", "\\n") + ": " +
                                        stats.getQueryRows().get(queryStr))
                                        .collect(Collectors.joining(";")) + "\"\n";
            }
        }

        File statisticsFile = new File(subResultsDir + "/layers-runtimes.csv");
        PrintWriter statisticsWriter = new PrintWriter(statisticsFile);
        statisticsWriter.write(runtimeStatistics);
        statisticsWriter.close();

        Files.write(Paths.get(resultsDirectory + "/summary.csv"), csvGenerator.getCSV().getBytes());

        // Do garbage collection because otherwise the benchmark crashes due to OOM ...
        System.gc();
        System.runFinalization();
    }

    public void run(File resultsDirectory, SummarizedResultsCSVGenerator csvGenerator) {
        try {
            List<BenchmarkConf> confs = generateBenchmarkConfigs();

            for (BenchmarkConf conf : confs) {
                if (enumerateJoinTrees) {
                    List<BenchmarkResult> benchmarkResults = enumerateBenchmark(conf);

                    for (BenchmarkResult result : benchmarkResults) {
                        System.out.println(conf.getSuffix());
                        if (conf.getSuffix().matches(".*t[0-9]+$")) {
                            conf.setSuffix(conf.getSuffix().replaceFirst("-t[0-9]+$", "-t" + result.getJoinTreeNo()));
                        }
                        else {
                            conf.setSuffix(conf.getSuffix() + "-t" + result.getJoinTreeNo());
                        }
                        saveBenchmarkData(conf, result, resultsDirectory, csvGenerator);
                    }
                }
                else {
                    BenchmarkResult res = benchmark(conf);
                    saveBenchmarkData(conf, res, resultsDirectory, csvGenerator);
                }
            }
            System.out.println("Saved benchmark results to: " + resultsDirectory.toString());
        } catch (SQLException | IOException | QueryConversionException | InterruptedException | TableNotFoundException e) {
            System.out.println("Error benchmarking: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<BenchmarkResult> getResults() {
        return results;
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

    public void setUseStatistics(boolean useStatistics) {
        this.useStatistics = useStatistics;
    }

    public void setBooleanQuery(boolean booleanQuery) {
        this.booleanQuery = booleanQuery;
    }

    public void setKeepTables(boolean keepTables) {
        this.keepTables = keepTables;
    }

    public void setAnalyzeQuery(boolean analyzeQuery) {
        this.analyzeQuery = analyzeQuery;
    }

    public void setInsertData(boolean insertData) {
        this.insertData = insertData;
    }

    public void setNoReinsert(boolean noReinsert) {
        this.noReinsert = noReinsert;
    }

    public void setDropTables(boolean dropTables) {
        this.dropTables = dropTables;
    }

    public void setCreateIndexes(boolean createIndexes) {
        this.createIndexes = createIndexes;
    }

    public void setDepthOpt(boolean depthOpt) {
        this.depthOpt = depthOpt;
    }

    public void setAcyclicOpt(boolean acyclicOpt) {
        this.acyclicOpt = acyclicOpt;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    public void setEnumerateJoinTrees(boolean enumerateJoinTrees) {
        this.enumerateJoinTrees = enumerateJoinTrees;
    }
}
