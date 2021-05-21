package hypergraph;

import at.ac.tuwien.dbai.hgtools.hypergraph.Edge;
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
    private Set<Hyperedge> edges = new HashSet<>();
    private Set<String> nodes = new HashSet<>();
    private Map<String, Hyperedge> edgesByName = new HashMap<>();
    // Maps column -> variable
    private Map<String, String> columnToVariableMapping = new HashMap<>();
    // Maps variable -> (table -> [column])
    private Map<String, Map<String, List<String>>> inverseEquivalenceMapping = new HashMap<>();
    // Maps variable -> [column] (table.colname)
    private Map<String, List<String>> variableToColumnMapping = new HashMap<>();

    protected Graph<HypertreeNode, DefaultEdge> decompositionTree;

    public Hypergraph() {

    }

    public Hypergraph(Set<String> nodes, Set<Hyperedge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        populateEdgesByNameMap();
    }

    public static Hypergraph fromDTL(String dtlString) {
        Set<Hyperedge> hgHyperedges = new HashSet<>();
        Set<String> hgNodes = new HashSet<>();

        String[] matches = Pattern.compile("(\\w+\\([,\\w\\s]*\\))")
                .matcher(dtlString)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);

        for (String match : matches) {
            Matcher matcher = Pattern.compile("(\\w+)\\(([,\\w\\s]*)\\)").matcher(match);

            if (matcher.find()) {
                String heName = matcher.group(1);
                Set<String> nodes = Set.of(matcher.group(2).split(","));
                hgHyperedges.add(new Hyperedge(heName, nodes));
                hgNodes.addAll(nodes);
            }
        }

        return new Hypergraph(hgNodes, hgHyperedges);
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
        return hypertreeToJoinTree(htNode, new HashSet<>());
        // Keep track of the tables that were already added to avoid adding contained tables twice
    }

    public JoinTreeNode hypertreeToJoinTree(HypertreeNode htNode, Set<String> assignedEdges) {
        // Recursively convert the hypertree data structure of jgrapht
        // to the simpler JoinTree
        JoinTreeNode root = new JoinTreeNode();
        List<String> tables = new LinkedList<>(htNode.getHyperedges());
        Set<String> attributesSet = new HashSet<>(htNode.getAttributes());

        assignedEdges.addAll(htNode.getHyperedges());

        root.setTables(tables);
        root.setAttributes(htNode.getAttributes());

        for (DefaultEdge edge : decompositionTree.outgoingEdgesOf(htNode)) {
            HypertreeNode childHTNode = decompositionTree.getEdgeTarget(edge);

            JoinTreeNode childJTNode = hypertreeToJoinTree(childHTNode, assignedEdges);
            childJTNode.setPredecessor(root);
            root.getSuccessors().add(childJTNode);
        }

        // Determine which edges are fully contained in the tree node
        Set<Hyperedge> containedEdges = new HashSet<>();

        for (Hyperedge edge : edges) {
            if (!tables.contains(edge.getName())
                    && attributesSet.containsAll(edge.getNodes())) {
                containedEdges.add(edge);
            }
        }

        // Add the contained edges
        // This is called from the bottom up so the deepest nodes are added first
        for (Hyperedge containedEdge : containedEdges) {
            if (!tables.contains(containedEdge.getName()) &&
                    !assignedEdges.contains(containedEdge.getName())) {
                JoinTreeNode newChildNode = new JoinTreeNode();
                newChildNode.setPredecessor(root);
                newChildNode.setTables(List.of(containedEdge.getName()));
                newChildNode.setAttributes(new LinkedList<>(containedEdge.getNodes()));
                root.getSuccessors().add(newChildNode);

                assignedEdges.add(containedEdge.getName());
            }
        }

        return root;
    }

    /**
     * Generate a join tree with default options
     *
     * @return
     * @throws JoinTreeGenerationException
     */
    public JoinTreeNode toJoinTree() throws JoinTreeGenerationException {
        return toJoinTree(new DecompositionOptions());
    }

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

    public List<JoinTreeNode> getRandomJoinTrees(int n) {
        return new LinkedList<>();
    }

    public JoinTreeNode toJoinTree(int hypertreeWidth, DecompositionOptions options) throws JoinTreeGenerationException {
        // Write hypergraph out to a file
        String fileContent = toDTL();
        File hgFile;
        try {
            hgFile = File.createTempFile("hypergraph", ".dtl");
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

        // Call the decomposition process

        if (options.getAlgorithm() == DecompositionOptions.DecompAlgorithm.DETKDECOMP) {
            try {
                Process process = new ProcessBuilder("detkdecomp", hypertreeWidth + "",
                        hgFile.getAbsolutePath()).start();

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String output = br.lines().collect(Collectors.joining());

                if (Pattern.compile("Hypertree of width \\d+ not found").matcher(output).find()) {
                    return null;
                }
            } catch (IOException e) {
                throw new JoinTreeGenerationException("Error executing detkdecomp");
            }
        } else if (options.getAlgorithm() == DecompositionOptions.DecompAlgorithm.BALANCEDGO) {
            try {
                Process process = new ProcessBuilder("BalancedGo",
                        "-width", hypertreeWidth + "", "-graph",
                        hgFile.getAbsolutePath(),
                        "-balDet", "1", "-gml", htFileName).start();

                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String output = br.lines().collect(Collectors.joining());

                if (Pattern.compile("Correct:  false").matcher(output).find()) {
                    return null;
                }
            } catch (IOException e) {
                throw new JoinTreeGenerationException("Error executing BalancedGo");
            }
        }

        decompositionTree = new SimpleDirectedGraph<HypertreeNode, DefaultEdge>(DefaultEdge.class);

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

    public String toDTL() {
        List<String> edgeStrings = new LinkedList<>();
        for (Hyperedge edge : edges) {
            edgeStrings.add(edge.getName() + "(" + String.join(",", edge.getNodes()) + ")");
        }

        return String.join(",\n", edgeStrings) + ".";
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

    private at.ac.tuwien.dbai.hgtools.hypergraph.Hypergraph toAlternativeHypergraph() {
        at.ac.tuwien.dbai.hgtools.hypergraph.Hypergraph otherHG = new at.ac.tuwien.dbai.hgtools.hypergraph.Hypergraph();
        for (Hyperedge edge : edges) {
            otherHG.addEdge(new Edge(edge.getName(), edge.getNodes().toArray(new String[0])));
        }
        return otherHG;
    }

    public int getDegree() {
        return toAlternativeHypergraph().degree();
    }

    public int getVCDimension() {
        return toAlternativeHypergraph().vcDimension();
    }

    public int getBIP() {
        return toAlternativeHypergraph().cntBip(2);
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

    public Map<String, String> getColumnToVariableMapping() {
        return columnToVariableMapping;
    }

    public void setColumnToVariableMapping(Map<String, String> columnToVariableMapping) {
        this.columnToVariableMapping = columnToVariableMapping;

        this.inverseEquivalenceMapping = new HashMap<>();
        this.variableToColumnMapping = new HashMap<>();

        HashSet<String> variablesSet = new HashSet<>(columnToVariableMapping.values());
        for (String variable : variablesSet) {
            inverseEquivalenceMapping.put(variable, new HashMap<String, List<String>>());
            variableToColumnMapping.put(variable, new LinkedList<>());
        }
        for (String columnIdentifier : columnToVariableMapping.keySet()) {
            String[] splits = columnIdentifier.split("\\.");
            String columnName = splits[1];
            String tableName = splits[0];
            String variableName = columnToVariableMapping.get(columnIdentifier);
            if (!inverseEquivalenceMapping.get(variableName).containsKey(tableName)) {
                inverseEquivalenceMapping.get(variableName).put(tableName, new LinkedList<>());
            }
            inverseEquivalenceMapping.get(variableName).get(tableName).add(columnName);
            variableToColumnMapping.get(variableName).add(columnIdentifier);
        }
    }

    public Map<String, Map<String, List<String>>> getInverseEquivalenceMapping() {
        return inverseEquivalenceMapping;
    }

    public Map<String, List<String>> getVariableToColumnMapping() {
        return variableToColumnMapping;
    }

    @Override
    public String toString() {
        return "Hypergraph{" +
                "edges=" + edges +
                ", nodes=" + nodes +
                '}';
    }
}
