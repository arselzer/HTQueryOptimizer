package hypergraph;

import exceptions.JoinTreeGenerationException;
import hypergraph.visualization.HypergraphVisualizer;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import query.JoinTreeNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Hypergraph {
    private static int HYPERTREE_WIDTH = 2;

    private Set<Hyperedge> edges = new HashSet<>();
    private Set<String> nodes = new HashSet<>();
    private Map<String, Hyperedge> edgesByName = new HashMap<>();

    // Maps column -> variable
    private Map<String, String> equivalenceMapping = new HashMap<>();
    // Maps variable -> (table -> column)
    private Map<String, Map<String, String>> inverseEquivalenceMapping = new HashMap<>();

    private Graph<HypertreeNode, DefaultEdge> decompositionTree;

    public Hypergraph() {

    }

    public Hypergraph(Set<String> nodes, Set<Hyperedge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        populateEdgesByNameMap();
    }

    public boolean isAcyclic() {
        // Perform GYO reduction
        return false; // TODO implement
    }

    private void populateEdgesByNameMap() {
        edgesByName.clear();
        for (Hyperedge edge : this.edges) {
            edgesByName.put(edge.getName(), edge);
        }
    }

    public String generateHGFileName() {
        //return "hypergraph";
        return String.format("hypergraph-%s", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
    }

    public JoinTreeNode hypertreeToJoinTree(HypertreeNode htNode) {
        // Recursively convert the hypertree data structure of jgrapht
        // to the simpler JoinTree
        JoinTreeNode root = new JoinTreeNode();
        root.setTables(htNode.getHyperedges());
        root.setAttributes(htNode.getAttributes());
        System.out.println(htNode.getHyperedges());;

        for (DefaultEdge edge : decompositionTree.outgoingEdgesOf(htNode)) {
            HypertreeNode childHTNode = decompositionTree.getEdgeTarget(edge);

            JoinTreeNode childJTNode = hypertreeToJoinTree(childHTNode);
            childJTNode.setPredecessor(root);
            root.getSuccessors().add(childJTNode);
        }

        return root;
    }

    public JoinTreeNode toJoinTree() throws JoinTreeGenerationException {
        // Try creating an acyclic join tree first
        int hypertreeWidth = 1;

        JoinTreeNode tree = toJoinTree(1);
        while (tree == null) {
            //System.out.printf("Hypertree of width %d not found\n", hypertreeWidth);;
            hypertreeWidth++;
            tree = toJoinTree(hypertreeWidth);
        }

        return tree;
    }

    public JoinTreeNode toJoinTree(int hypertreeWidth) throws JoinTreeGenerationException {
        // Write hypergraph out to a file
        String fileContent = toDTL();
        File hgFile;
        try {
            hgFile= File.createTempFile("hypergraph", ".dtl");
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Could not create temporary file: " + e.getMessage());
        }

        String htFileName = hgFile.getAbsolutePath().replace(".dtl", ".gml");

        try {
            PrintWriter out = new PrintWriter(hgFile);

            out.write(fileContent);

            out.close();
        } catch (FileNotFoundException e) {
            throw new JoinTreeGenerationException("Error writing hypergraph file: " + e.getMessage());
        }

        // Call detkdecomp

        try {
            Process process = new ProcessBuilder("detkdecomp", hypertreeWidth + "",
                    hgFile.getAbsolutePath().toString()).start();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String output = br.lines().collect(Collectors.joining());

            if (Pattern.compile("Hypertree of width \\d+ not found").matcher(output).find()) {
                return null;
            }
        } catch (IOException e) {
            throw new JoinTreeGenerationException("Error executing detkdecomp");
        }

        decompositionTree = new SimpleDirectedGraph<HypertreeNode, DefaultEdge>(DefaultEdge.class);

        VertexProvider<HypertreeNode> vp = new VertexProvider<>() {
            private Pattern edgePattern = Pattern.compile("^\\s*\\{(.*)\\}\\s*\\{(.*)\\}");

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

        EdgeProvider<HypertreeNode, DefaultEdge> ep = new EdgeProvider<HypertreeNode, DefaultEdge>() {
            @Override
            public DefaultEdge buildEdge(HypertreeNode from, HypertreeNode to, String label, Map<String, Attribute> map) {
                return decompositionTree.addEdge(from, to);
            }
        };

        // Read the output of detkdecomp
        try {
            GmlImporter<HypertreeNode, DefaultEdge> importer = new GmlImporter<>(vp, ep);

            importer.importGraph(decompositionTree, new File(htFileName));

            Iterator<HypertreeNode> it = new BreadthFirstIterator<>(decompositionTree);

            HypertreeNode root = null;
            for (HypertreeNode n : decompositionTree.vertexSet()) {
                if (n.id.equals("1")) {
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

    public String toDTL() {
        List<String> edgeStrings = new LinkedList<>();
        for (Hyperedge edge : edges) {
            edgeStrings.add(edge.getName() + "(" + String.join(",", edge.getNodes()) + ")");
        }

        return String.join(",\n", edgeStrings) + ".";
    }

    public static Hypergraph fromDTL(String dtlString) {

        Set<Hyperedge> hgHyperedges = new HashSet<>();
        Set<String> hgNodes = new HashSet<>();

        String[] matches = Pattern.compile("(\\w+\\([,\\d\\s]*\\))")
                .matcher(dtlString)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);

        for (String match : matches) {
            Matcher matcher = Pattern.compile("(\\w+)\\(([,\\d\\s]*)\\)").matcher(match);

            if (matcher.find()) {
                String heName = matcher.group(1);
                Set<String> nodes = Set.of(matcher.group(2).split(","));
                hgHyperedges.add(new Hyperedge(heName, nodes));
                hgNodes.addAll(nodes);
            }
        }

        return new Hypergraph(hgNodes, hgHyperedges);
    }

    public String toLaTeX() {
        return new HypergraphVisualizer(this).toLaTeX();
    }

    public Path toPDF() throws IOException, InterruptedException {
        return new HypergraphVisualizer(this).toPDF();
    }

    public void toPDF(Path filePath) throws IOException, InterruptedException {
        new HypergraphVisualizer(this).toPDF(filePath);
    }

    public void displayPDF() throws IOException, InterruptedException {
        new HypergraphVisualizer(this).displayPDF();
    }

    public Set<Hyperedge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Hyperedge> edges) {
        this.edges = edges;
        populateEdgesByNameMap();
    }

    public void addEdge(Hyperedge edge) {
        this.edges.add(edge);
        edgesByName.put(edge.getName(), edge);
    }

    public Hyperedge getEdgeByName(String name) {
        return edgesByName.get(name);
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    public void addNode(String node) {
        this.nodes.add(node);
    }

    public Map<String, String> getEquivalenceMapping() {
        return equivalenceMapping;
    }

    public Map<String, Map<String, String>> getInverseEquivalenceMapping() {
        return inverseEquivalenceMapping;
    }

    public void setEquivalenceMapping(Map<String, String> equivalenceMapping) {
        this.equivalenceMapping = equivalenceMapping;

        this.inverseEquivalenceMapping = new HashMap<>();

        HashSet<String> variablesSet = new HashSet<>(equivalenceMapping.values());
        for (String variable : variablesSet) {
            inverseEquivalenceMapping.put(variable, new HashMap<String, String>());
        }
        for (String columnIdentifier : equivalenceMapping.keySet()) {
            String[] splits = columnIdentifier.split("\\.");
            String columnName = splits[1];
            String tableName = splits[0];
            String variableName = equivalenceMapping.get(columnIdentifier);
            inverseEquivalenceMapping.get(variableName).put(tableName, columnName);
        }
    }

    @Override
    public String toString() {
        return "Hypergraph{" +
                "edges=" + edges +
                ", nodes=" + nodes +
                '}';
    }
}
