import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

public class HypertreeDecomposition {
    Graph<String, DefaultEdge> decompositionTree = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

    public HypertreeDecomposition(File gmlFile) {
        VertexProvider<String> vp = new VertexProvider<String>() {
            @Override
            public String buildVertex(String s, Map<String, Attribute> map) {
                return s;
            }
        };

        EdgeProvider<String, DefaultEdge> ep = new EdgeProvider<String, DefaultEdge>() {
            @Override
            public DefaultEdge buildEdge(String from, String to, String label, Map<String, Attribute> map) {
                return decompositionTree.addEdge(from, to);
            }
        };

        GmlImporter<String, DefaultEdge> importer = new GmlImporter<>(vp, ep);

        try {
            importer.importGraph(decompositionTree, gmlFile);

            System.out.println(decompositionTree);
        } catch (ImportException e) {
            System.err.println("Error importing graph " + e.getMessage());
        }
    }
}
