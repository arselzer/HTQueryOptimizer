package hypergraph;

import exceptions.JoinTreeGenerationException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import query.JoinTreeNode;
import schema.TableStatistics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WeightedHypergraph extends Hypergraph {
    private Set<BagWeight> weights;

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

    public WeightedHypergraph(Hypergraph hypergraph, TableStatistics statistics) {
        super(hypergraph.getNodes(), hypergraph.getEdges());
    }

    public Set<BagWeight> getWeights() {
        return weights;
    }

    public void setWeights(Set<BagWeight> weights) {
        this.weights = weights;
        // TODO complete init
    }

    public String toWeightsFile() {
        String output = "";
        for (BagWeight weight : weights) {
            output += weight.getBag().stream()
                    .map(Hyperedge::getName)
                    .collect(Collectors.joining(",")) + "," + weight.getWeight();
        }
        return output;
    }

    // TODO refactor copy paste
    @Override
    public JoinTreeNode toJoinTree(int hypertreeWidth, DecompositionOptions options) throws JoinTreeGenerationException {
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

            return hypertreeToJoinTree(root);
        } catch (ImportException e) {
            throw new JoinTreeGenerationException("Error importing hypertree file: " + e.getMessage());
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Error deleting temporary files: " + e.getMessage());
        }
    }
}
