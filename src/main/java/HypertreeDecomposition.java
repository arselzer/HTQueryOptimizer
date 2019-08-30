import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import query.DBSchema;
import query.JoinTree;
import query.Table;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HypertreeDecomposition {
    Graph<HypertreeNode, DefaultEdge> decompositionTree = new SimpleDirectedGraph<HypertreeNode, DefaultEdge>(DefaultEdge.class);
    Map<String, Set<String>> hyperedges = new HashMap<>();
    Map<String, String> variableNames = new HashMap<>();
    DBSchema schema = new DBSchema();

    private class HypertreeNode {
        String id;
        List<String> hyperedges;

        public HypertreeNode(String id, List<String> hyperedges) {
            this.id = id;
            this.hyperedges = hyperedges;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getHyperedges() {
            return hyperedges;
        }

        public void setHyperedges(List<String> hyperedges) {
            this.hyperedges = hyperedges;
        }

        @Override
        public String toString() {
            return "HypertreeNode{" +
                    "id='" + id + '\'' +
                    ", hyperedges=" + hyperedges +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HypertreeNode that = (HypertreeNode) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private void buildCQFromSQL(File sqlFile) {

    }

    /*private void readSchema(File schemaFile) {
        try {
            Files.lines(Paths.get(schemaFile.getAbsolutePath())).forEach((line) -> {
                String[] splits = line.split(";");
                String tableName = splits[0].trim();
                String[] colSplits = splits[1].split(",");
                List<String> columns = new LinkedList<>();
                for (String col : colSplits) {
                    columns.add(col.trim());
                }

                Table t = new Table();
                t.setName(tableName);
                t.setColumns(columns);
                schema.getTables().add(t);
            });
        } catch (IOException e) {
            System.err.println("Error reading schema file: " + e.getMessage());
        }
    }*/

    public HypertreeDecomposition(File hypertreeFile, File hypergraphFile, File sqlFile, File schemaFile) {
        buildCQFromSQL(sqlFile);
        //readSchema(schemaFile);

        System.out.println(schema);

        VertexProvider<HypertreeNode> vp = new VertexProvider<>() {
            private Pattern edgePattern = Pattern.compile("^\\s*\\{(.*)\\}");
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

                    return new HypertreeNode(id, edgesList);
                }
                else {
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

        try {
            String hypergraphContent = Files.lines(Paths.get(hypergraphFile.getAbsolutePath())).reduce("", (a, b) -> a + b);

            String[] matches = Pattern.compile("(\\w+\\([,\\d\\s]*\\))")
                    .matcher(hypergraphContent)
                    .results()
                    .map(MatchResult::group)
                    .toArray(String[]::new);

            for (String match : matches) {
                Matcher matcher = Pattern.compile("(\\w+)\\(([,\\d\\s]*)\\)").matcher(match);

                if (matcher.find()) {
                    String heName = matcher.group(1);
                    Set<String> nodes = Set.of(matcher.group(2).split(","));
                    hyperedges.put(heName, nodes);
                }
            }

            for (String name : hyperedges.keySet()) {
                System.out.println(name + ", " + hyperedges.get(name));

                // Populate table names
                // TODO parse SQL and determine mapping
                for (String var : hyperedges.get(name)) {
                    // For now just assume "t" before variable number
                    variableNames.putIfAbsent(var, "v" + var);
                }
            }

            try {
                GmlImporter<HypertreeNode, DefaultEdge> importer = new GmlImporter<>(vp, ep);

                importer.importGraph(decompositionTree, hypertreeFile);

                Iterator<HypertreeNode> it = new BreadthFirstIterator<>(decompositionTree);

                for (HypertreeNode node : decompositionTree.vertexSet()) {
                    System.out.println(node);
                    System.out.println(decompositionTree.outgoingEdgesOf(node));
                }

                //System.out.println(decompositionTree);
            } catch (ImportException e) {
                System.err.println("Error importing hypertree file " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error reading hypergraph file");
        }
    }

    public JoinTree toQueryTree() {
        return null;
    }

    public String toUnoptimizedQuery() {
        String query = "SELECT * FROM " + String.join(", ", variableNames.values()) + "\nWHERE ";


        return query;
    }

    public String toQuery() {
        return toUnoptimizedQuery();
    }

    public static void decomposeHypertree(String file) {
        try {
            Process process = new ProcessBuilder("detkdecomp", "1", file).start();
        } catch (IOException e) {
            System.err.println("Error executing detkdecomp");
        }
    }
}
