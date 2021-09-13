package hypergraph;

import exceptions.JoinTreeGenerationException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import query.JoinTreeNode;
import schema.TableStatistics;
import org.apache.commons.math3.util.Combinations;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WeightedHypergraph extends Hypergraph {
    private Set<BagWeight> weights;
    private Set<SemiJoinWeight> semiJoinWeights = new HashSet<>();
    private Map<String, TableStatistics> statistics;
    // Table aliases are required to find the real table names to access the statistics
    private Map<String, String> tableAliases;
    // Keep an ordered list of hyperedges for index access when using the combinations iterator
    List<Hyperedge> orderedEdges = new LinkedList<>(getEdges());

    private Connection connection;

    public WeightedHypergraph() {
        super();
    }

    // Weights are uninitialized
    public WeightedHypergraph(Set<String> nodes, Set<Hyperedge> edges, Map<String, String> tableAliases) {
        super(nodes, edges);
        this.weights = new HashSet<>();
        this.semiJoinWeights = new HashSet<>();
        this.tableAliases = tableAliases;
    }

    // Weights are set at construction
    public WeightedHypergraph(Set<String> nodes, Set<Hyperedge> edges, Set<BagWeight> weights) {
        super(nodes, edges);
        this.weights = weights;
    }

    // Add weights to an existing unweighted hypergraph
    public WeightedHypergraph(Hypergraph hypergraph, Map<String, TableStatistics> statistics, Map<String, String> tableAliases) {
        super(hypergraph.getVertices(), hypergraph.getEdges());
        this.tableAliases = tableAliases;
        this.setColumnToVariableMapping(hypergraph.getColumnToVariableMapping());
        this.weights = new HashSet<>();
        this.statistics = statistics;
    }

    public Set<BagWeight> getWeights() {
        return weights;
    }

    public void setWeights(Set<BagWeight> weights) {
        this.weights = weights;
    }

    private void determineWeightsForBagSize(int bagSize) {
        determineWeightsForBagSize(bagSize, false, false);
    }

    private void determineSemiJoinWeights() throws SQLException {
        for (int[] combination : new Combinations(getEdges().size(), 2)) {
            Set<Hyperedge> bag = new HashSet<>();

            for (int edgeIdx : combination) {
                Hyperedge edge = orderedEdges.get(edgeIdx);
                bag.add(edge);
            }
            // Keep only the attributes occurring in all relations
            Set<String> commonAttributes = new HashSet<>();
            commonAttributes.addAll(bag.iterator().next().getNodes());
            for (Hyperedge edge : bag) {
                commonAttributes.retainAll(edge.getNodes());
            }

            System.out.println(combination);
            System.out.println(commonAttributes);

            if (!commonAttributes.isEmpty()) {

                Hyperedge edge1 = orderedEdges.get(combination[0]);
                Hyperedge edge2 = orderedEdges.get(combination[1]);
                String edge1Name = orderedEdges.get(combination[0]).getName();
                String edge2Name = orderedEdges.get(combination[1]).getName();
                String table1Name = tableAliases.get(edge1Name);
                String table2Name = tableAliases.get(edge2Name);

                System.out.println("table names: " + table1Name + " " + table2Name);

                String joinConditions = commonAttributes.stream().map(attr -> {
                    return table1Name + "." + getInverseEquivalenceMapping().get(attr).get(edge1Name).get(0)
                            + " = " + getInverseEquivalenceMapping().get(attr).get(edge2Name).get(0);
                }).collect(Collectors.joining(" AND "));

                System.out.println(joinConditions);

                PreparedStatement explainSemiJoin1Statement = connection.prepareStatement(String.format(
                        "EXPLAIN SELECT * FROM %s WHERE EXISTS (SELECT 1 FROM %s WHERE %s);",
                        table1Name, table2Name, joinConditions));

                PreparedStatement explainSemiJoin2Statement = connection.prepareStatement(String.format(
                        "EXPLAIN SELECT * FROM %s WHERE EXISTS (SELECT 1 FROM %s WHERE %s);",
                        table2Name, table1Name, joinConditions));

                ResultSet rs1 = explainSemiJoin1Statement.executeQuery();
                ResultSet rs2 = explainSemiJoin2Statement.executeQuery();
                rs1.next();
                rs2.next();

                String explain1 = rs1.getString(1);
                String explain2 = rs2.getString(1);

                Pattern explainRowsPattern = Pattern.compile(" rows=(\\d+) ");
                Matcher matcher1 = explainRowsPattern.matcher(explain1);
                matcher1.find();
                String rows1Str = matcher1.group(1);
                Matcher matcher2 = explainRowsPattern.matcher(explain2);
                matcher2.find();
                String rows2Str = matcher2.group(1);

                double rows1 = Double.parseDouble(rows1Str);
                double rows2 = Double.parseDouble(rows2Str);

                semiJoinWeights.add(new SemiJoinWeight(List.of(edge1, edge2), rows1));
                semiJoinWeights.add(new SemiJoinWeight(List.of(edge2, edge1), rows2));
            }
        }
    }

    public void useConnection(Connection connection) {
        this.connection = connection;
    }

    private void determineWeightsForBagSize(int bagSize, boolean computeSelectivity, boolean negate) {
        if (tableAliases == null) {
            throw new IllegalStateException("Table aliases are not set");
        }

        for (int[] combination : new Combinations(getEdges().size(), bagSize)) {
            Set<Hyperedge> bag = new HashSet<>();

            for (int edgeIdx : combination) {
                Hyperedge edge = orderedEdges.get(edgeIdx);
                bag.add(edge);
            }
            // Keep only the attributes occurring in all relations
            Set<String> commonAttributes = new HashSet<>();
            commonAttributes.addAll(bag.iterator().next().getNodes());
            for (Hyperedge edge : bag) {
                commonAttributes.retainAll(edge.getNodes());
            }

            if (bagSize == 1) {
                // If the bag contains one hyperedge, there are no common attributes
                commonAttributes.clear();
            }

            //System.out.println("bag: " + bag);
            System.out.println("commonAttributes: " + commonAttributes);

            Double weight = 1.0;
            for (String attribute : commonAttributes) {
                //System.out.println("common attribute: " + attribute);
                // The values with frequencies which occur in all joined attributes
                // First, start with the common vals of one edge
                Iterator<Hyperedge> edgeIterator = bag.iterator();
                Hyperedge firstEdge = edgeIterator.next();
                String realTableName = tableAliases.get(firstEdge.getName());
                String firstEdgeColumnName = getInverseEquivalenceMapping().get(attribute).get(firstEdge.getName()).get(0);
                Map<String, Map<String, Double>> firstEdgeMostCommonFrequencies = statistics.get(realTableName)
                        .getMostCommonFrequencies();
                Set<String> sharedCommonValues = new HashSet<>();
                if (!firstEdgeMostCommonFrequencies.isEmpty()) {
                    sharedCommonValues.addAll(firstEdgeMostCommonFrequencies.get(firstEdgeColumnName).keySet());
                }

                for (Hyperedge edge : bag) {
                    TableStatistics tableStats = statistics.get(tableAliases.get(edge.getName()));
                    // TODO for simplicity the case of multiple equal columns in the same table is not considered
                    String columnName = getInverseEquivalenceMapping().get(attribute).get(edge.getName()).get(0);
                    Map<String, Double> mostCommonFrequencies = tableStats.getMostCommonFrequencies().get(columnName);
                    System.out.println("most common freqs of " + edge + ": " + mostCommonFrequencies);
                    if (mostCommonFrequencies != null) {
                        Set<String> commonVals = mostCommonFrequencies.keySet();
                        sharedCommonValues.retainAll(commonVals);
                    }
                }

                Double columnSelectivity = 0.0;
                for (String value : sharedCommonValues) {
                    //System.out.println("shared common value: " + value);
                    List<Double> frequencies = new LinkedList<>();
                    for (Hyperedge edge : bag) {
                        String columnName = getInverseEquivalenceMapping().get(attribute).get(edge.getName()).get(0);
                        Map<String, Double> valueToFrequencyMap = statistics.get(tableAliases.get(edge.getName()))
                                .getMostCommonFrequencies().get(columnName);
                        if (valueToFrequencyMap != null) {
                            Double frequency = valueToFrequencyMap.get(value);
                            if (frequency != null) {
                                frequencies.add(frequency);
                                //System.out.println("frequency added " + frequency);
                                //System.out.println(edge.getName() + " " + statistics.get(edge.getName()).getMostCommonFrequencies());
                            }
                        }
                    }
                    if (!frequencies.isEmpty()) {
                        //System.out.println("frequencies: " + frequencies);
                        columnSelectivity += frequencies.stream().reduce(1.0, (a, b) -> a * b);
                    }
                }
                System.out.println("sharedCommonValues: " + sharedCommonValues);
                if (sharedCommonValues.isEmpty()) {
                    // Cross product
                    columnSelectivity = 1.0;
                }

                weight *= columnSelectivity;
                //System.out.println("column selectivity: " + columnSelectivity);
            }
            System.out.println(bag);
            System.out.println(weight);

            // Multiply the join selectivity with the row count of the cross product
            if (!computeSelectivity) {
                for (Hyperedge edge : bag) {
                    weight *= statistics.get(tableAliases.get(edge.getName())).getRowCount();
                    //System.out.println("edge: " + edge + ", row count: " + statistics.get(edge.getName()).getRowCount() + ", weight: " + weight);
                }
            }
            //System.out.println("total selectivity: " + weight + ", row estimate: " + weight);

            // Make the weight negative if the query is acyclic
            if (!computeSelectivity) {
                if (bagSize == 1 && negate) {
                    weight *= -1;
                }
            }

            weights.add(new BagWeight(bag, weight));
        }
    }

    public String toWeightsFile() {
        String output = "";
        for (BagWeight weight : weights) {
            output += weight.getBag().stream()
                    .map(Hyperedge::getName)
                    .collect(Collectors.joining(",")) + "," + String.format("%.4f", weight.getWeight()) + "\n";
        }
        for (SemiJoinWeight weight : semiJoinWeights) {
            output += weight.getBag().stream()
                    .map(Hyperedge::getName)
                    .collect(Collectors.joining(",")) + "," + String.format("%.4f", weight.getWeight()) + "\n";
        }
        return output;
    }

    @Override
    public JoinTreeNode toJoinTree() throws JoinTreeGenerationException {
        return toJoinTree(new DecompositionOptions());
    }

    @Override
    public JoinTreeNode toJoinTree(DecompositionOptions options) throws JoinTreeGenerationException {
        // Try creating an acyclic join tree first
        int hypertreeWidth = 1;

        //return toAcyclicJoinTree(options); // todo remove

        JoinTreeNode tree = toJoinTree(1, options);
        while (tree == null) {
            hypertreeWidth++;
            tree = toJoinTree(hypertreeWidth, options);
        }

        return tree;
    }

    public JoinTreeNode toAcyclicJoinTree(DecompositionOptions options) throws JoinTreeGenerationException {
        determineWeightsForBagSize(1, false, false);
        try {
            determineSemiJoinWeights();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // Write hypergraph out to a file
        String fileContent = toDTL();
        String weightFileContent = toWeightsFile();
        File hgFile;
        File weightsFile;
        try {
            hgFile = File.createTempFile("hypergraph", ".dtl");
            weightsFile = File.createTempFile("weights", ".csv");
            hgFile = Paths.get("hypergraph.dtl").toFile();
            weightsFile = Paths.get("weights.csv").toFile();
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Could not create temporary file: " + e.getMessage());
        }

        String htFileName = hgFile.getAbsolutePath().replace(".dtl", ".gml");

        try {
            PrintWriter hgFileWriter = new PrintWriter(hgFile);
            hgFileWriter.write(fileContent);
            hgFileWriter.close();

            PrintWriter weightFileWriter = new PrintWriter(weightsFile);
            weightFileWriter.write(weightFileContent);
            weightFileWriter.close();
        } catch (FileNotFoundException e) {
            throw new JoinTreeGenerationException("Error writing to file: " + e.getMessage());
        }

        // Call the decomposition process

            try {
                Process process;
                if (options.isDepthOpt()) {
                    process = new ProcessBuilder("python3",
                            "jointree_search/jointree_gen/main.py", hgFile.getAbsolutePath(), weightsFile.getAbsolutePath(),
                            htFileName, "--depth").start();
                }
                else {
                    process = new ProcessBuilder("python3",
                            "jointree_search/jointree_gen/main.py", hgFile.getAbsolutePath(), weightsFile.getAbsolutePath(),
                            htFileName).start();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String output = br.lines().collect(Collectors.joining());

                System.out.println(output);

                if (output.contains("RecursionError")) {
                    return null;
                }
            } catch (IOException e) {
                throw new JoinTreeGenerationException("Error executing python");
            }

        decompositionTree = new SimpleDirectedGraph<>(DefaultEdge.class);

        VertexProvider<HypertreeNode> vp = new VertexProvider<>() {
            private Pattern edgePattern = Pattern.compile("^\\s*\\{(.*)\\}\\s*[\\{\\(](.*)[\\}\\)]");

            @Override
            public HypertreeNode buildVertex(String id, Map<String, Attribute> map) {
                String label = map.get("label").getValue();
                Matcher edgeMatcher = edgePattern.matcher(label);
                if (edgeMatcher.find()) {
                    String[] edges = edgeMatcher.group(1).split(",");
                    List<String> edgesList = new LinkedList<>();
                    // Remove whitespace from hyperedges
                    for (String edge : edges) {
                        edgesList.add(edge.trim());
                    }

                    String[] variables = edgeMatcher.group(2).split(",");

                    List<String> variablesList = new LinkedList<>();
                    // Remove whitespace from hyperedges
                    for (String var : variables) {
                        variablesList.add(var.trim());
                    }

                    return new HypertreeNode(id, edgesList, variablesList);
                } else {
                    // In case there is an empty list ...
                    return null;
                }
            }
        };

        EdgeProvider<HypertreeNode, DefaultEdge> ep = (from, to, label, map) -> decompositionTree.addEdge(from, to);

        // Import the GML file
        try {
            GmlImporter<HypertreeNode, DefaultEdge> importer = new GmlImporter<>(vp, ep);

            importer.importGraph(decompositionTree, new File(htFileName));

            Iterator<HypertreeNode> it = new BreadthFirstIterator<>(decompositionTree);

            HypertreeNode root = null;
            for (HypertreeNode n : decompositionTree.vertexSet()) {
                // In the case of detkdecomp n.id == 0 can also be checked
                if (decompositionTree.inDegreeOf(n) == 0) {
                    root = n;
                }
            }

            hgFile.delete();
            Files.delete(Paths.get(htFileName));

            System.out.println("join tree from gml " + hypertreeToJoinTree(root));
            return hypertreeToJoinTree(root);
        } catch (ImportException e) {
            throw new JoinTreeGenerationException("Error importing hypertree file: " + e.getMessage());
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Error deleting temporary files: " + e.getMessage());
        }
    }

    // TODO refactor copy paste
    @Override
    public JoinTreeNode toJoinTree(int hypertreeWidth, DecompositionOptions options) throws JoinTreeGenerationException {
        System.out.println("Attempting to create hypertree of width " + hypertreeWidth);

        if (hypertreeWidth == 1) {
            return toAcyclicJoinTree(options);
        }

        determineWeightsForBagSize(hypertreeWidth);

        // Write hypergraph out to a file
        String fileContent = toDTL();
        String weightFileContent = toWeightsFile();
        File hgFile;
        File weightsFile;
        try {
            hgFile = File.createTempFile("hypergraph", ".dtl");
            weightsFile = File.createTempFile("weights", ".csv");
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Could not create temporary file: " + e.getMessage());
        }

        String htFileName = hgFile.getAbsolutePath().replace(".dtl", ".gml");

        try {
            PrintWriter out = new PrintWriter(hgFile);
            out.write(fileContent);
            out.close();

            PrintWriter weightFileWriter = new PrintWriter(weightsFile);
            weightFileWriter.write(weightFileContent);
            weightFileWriter.close();
        } catch (FileNotFoundException e) {
            throw new JoinTreeGenerationException("Error writing to file: " + e.getMessage());
        }

        // Call the decomposition process

        if (options.getAlgorithm() == DecompositionOptions.DecompAlgorithm.DETKDECOMP) {
            throw new IllegalArgumentException("Decomposing a weighted hypergraph is not possible with detkdecomp");
        } else if (options.getAlgorithm() == DecompositionOptions.DecompAlgorithm.BALANCEDGO) {
            try {
                Process process = new ProcessBuilder("BalancedGo",
                        "-width", hypertreeWidth + "", "-graph",
                        hgFile.getAbsolutePath(),
                        "-local", "-cpu", "1",
                        "-joinCost", weightsFile.getAbsolutePath(),
                        "-gml", htFileName).start();

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String output = br.lines().collect(Collectors.joining());

                if (Pattern.compile("Correct:  false").matcher(output).find()) {
                    return null;
                }
            } catch (IOException e) {
                throw new JoinTreeGenerationException("Error executing BalancedGo");
            }
        }

        decompositionTree = new SimpleDirectedGraph<>(DefaultEdge.class);

        VertexProvider<HypertreeNode> vp = new VertexProvider<>() {
            private Pattern edgePattern = Pattern.compile("^\\s*\\{(.*)\\}\\s*[\\{\\(](.*)[\\}\\)]");

            @Override
            public HypertreeNode buildVertex(String id, Map<String, Attribute> map) {
                String label = map.get("label").getValue();
                Matcher edgeMatcher = edgePattern.matcher(label);
                if (edgeMatcher.find()) {
                    String[] edges = edgeMatcher.group(1).split(",");
                    List<String> edgesList = new LinkedList<>();
                    // Remove whitespace from hyperedges
                    for (String edge : edges) {
                        edgesList.add(edge.trim());
                    }

                    String[] variables = edgeMatcher.group(2).split(",");

                    List<String> variablesList = new LinkedList<>();
                    // Remove whitespace from hyperedges
                    for (String var : variables) {
                        variablesList.add(var.trim());
                    }

                    return new HypertreeNode(id, edgesList, variablesList);
                } else {
                    // In case there is an empty list ...
                    return null;
                }
            }
        };

        EdgeProvider<HypertreeNode, DefaultEdge> ep = (from, to, label, map) -> decompositionTree.addEdge(from, to);

        // Import the GML file
        try {
            GmlImporter<HypertreeNode, DefaultEdge> importer = new GmlImporter<>(vp, ep);

            importer.importGraph(decompositionTree, new File(htFileName));

            Iterator<HypertreeNode> it = new BreadthFirstIterator<>(decompositionTree);

            HypertreeNode root = null;
            for (HypertreeNode n : decompositionTree.vertexSet()) {
                // In the case of detkdecomp n.id == 0 can also be checked
                if (decompositionTree.inDegreeOf(n) == 0) {
                    root = n;
                }
            }

            hgFile.delete();
            Files.delete(Paths.get(htFileName));

            System.out.println("join tree from gml " + hypertreeToJoinTree(root));
            return hypertreeToJoinTree(root);
        } catch (ImportException e) {
            throw new JoinTreeGenerationException("Error importing hypertree file: " + e.getMessage());
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Error deleting temporary files: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "WeightedHypergraph{" +
                "vertices=" + getVertices() +
                ", edges=" + getEdges() +
                ", weights=" + weights +
                ", statistics=" + statistics +
                '}';
    }
}
