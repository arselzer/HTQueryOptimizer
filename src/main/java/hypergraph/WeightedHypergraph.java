package hypergraph;

import at.ac.tuwien.dbai.hgtools.util.CombinationIterator;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WeightedHypergraph extends Hypergraph {
    private Set<BagWeight> weights;
    private Map<String, TableStatistics> statistics;
    // Keep an ordered list of hyperedges for index access when using the combinations iterator
    List<Hyperedge> orderedEdges = new LinkedList<>(getEdges());

    public WeightedHypergraph() {
        super();
    }

    public WeightedHypergraph(Set<String> nodes, Set<Hyperedge> edges) {
        super(nodes, edges);
        this.weights = new HashSet<>();
    }

    public WeightedHypergraph(Set<String> nodes, Set<Hyperedge> edges, Set<BagWeight> weights) {
        super(nodes, edges);
        this.weights = weights;
    }

    public WeightedHypergraph(Hypergraph hypergraph, Map<String, TableStatistics> statistics) {
        super(hypergraph.getNodes(), hypergraph.getEdges());
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
        Iterator it = new Combinations(getEdges().size(), bagSize).iterator();
        while (it.hasNext()) {
            int[] combination = (int[]) it.next();
            Set<Hyperedge> bag = new HashSet<>();
            for (int i = 0; i < combination.length; i++) {
                Hyperedge edge = orderedEdges.get(combination[i]);
                bag.add(edge);
            }
            // Keep only the attributes occurring in all relations
            Set<String> commonAttributes = new HashSet<>();
            commonAttributes.addAll(bag.iterator().next().getNodes());
            for (Hyperedge edge : bag) {
                commonAttributes.retainAll(edge.getNodes());
            }

            System.out.println("bag: " + bag);

            Double weight = 1.0;
            for (String attribute : commonAttributes) {
                System.out.println("common attribute: " + attribute);
                // The values with frequencies which occur in all joined attributes
                Set<String> sharedFrequentValues = new HashSet<>();
                for (Hyperedge edge : bag) {
                    String tableName = edge.getName();
                    TableStatistics tableStats = statistics.get(tableName);
                    // TODO for simplicity the case of multiple equal columns in the same table is not considered
                    String columnName = getInverseEquivalenceMapping().get(attribute).get(tableName).get(0);
                    // Sometimes, if very few values exist in a table, no statistics are calculated and no values are set of the column
                    Map<String, Double> mostCommonFrequencies = tableStats.getMostCommonFrequencies().get(columnName);
                    if (mostCommonFrequencies != null) {
                        Set<String> commonVals = mostCommonFrequencies.keySet();
                        for (String val : commonVals) {
                            sharedFrequentValues.add(val);
                        }
                    }
                }

                Double columnSelectivity = 0.0;
                for (String value : sharedFrequentValues) {
                    System.out.println("shared frequent value: " + value);
                    List<Double> frequencies = new LinkedList<>();
                    for (Hyperedge edge : bag) {
//                        System.out.println("value to frequency map: " + statistics.get(edge.getName()).getMostCommonFrequencies() + ", attribute: " + attribute);
//                        System.out.println("value to frequency: " + statistics.get(edge.getName()).getMostCommonFrequencies().get(attribute));
                        String columnName = getInverseEquivalenceMapping().get(attribute).get(edge.getName()).get(0);
                        Map<String, Double> valueToFrequencyMap = statistics.get(edge.getName()).getMostCommonFrequencies().get(columnName);
                        if (valueToFrequencyMap != null) {
                            Double frequency = valueToFrequencyMap.get(value);
                            if (frequency != null) {
                                frequencies.add(frequency);
                                //System.out.println(edge.getName() + " " + statistics.get(edge.getName()).getMostCommonFrequencies());
                            }
                        }
                    }
                    if (!frequencies.isEmpty()) {
                        columnSelectivity += frequencies.stream().reduce(1.0, (a,b) -> a * b);
                    }
                }
                if (sharedFrequentValues.isEmpty()) {
                    // Cross product
                    columnSelectivity = 1.0;
                }

                weight *= columnSelectivity;
            }

            // Multiply the join selectivity with the row count of the cross product
            for (Hyperedge edge : bag) {
                weight *= statistics.get(edge.getName()).getRowCount();
                System.out.println("edge: " + edge + ", row count: " + statistics.get(edge.getName()).getRowCount() + ", weight: " + weight);
            }

            weights.add(new BagWeight(bag, weight));
        }
    }

    public String toWeightsFile() {
        String output = "";
        for (BagWeight weight : weights) {
            output += weight.getBag().stream()
                    .map(Hyperedge::getName)
                    .collect(Collectors.joining(",")) + "," + String.format("%.3f", weight.getWeight()) + "\n";
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


        JoinTreeNode tree = toJoinTree(1, options);
        while (tree == null) {
            hypertreeWidth++;
            tree = toJoinTree(hypertreeWidth, options);
        }

        return tree;
    }

    // TODO refactor copy paste
    @Override
    public JoinTreeNode toJoinTree(int hypertreeWidth, DecompositionOptions options) throws JoinTreeGenerationException {
        System.out.println("Attempting to create hypertree of width " + hypertreeWidth);

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
                "vertices=" + getNodes() +
                ", edges=" + getEdges() +
                ", weights=" + weights +
                ", statistics=" + statistics +
                '}';
    }
}
