

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.io.GmlImporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String gmlFileName = args[0];
        String dtlFileName = args[1];
        String sqlFileName = args[2];

        Graph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        try {
            String gml = Files.lines(Paths.get(gmlFileName)).reduce("", (a,b) -> a + b);

            HypertreeDecomposition hd = new HypertreeDecomposition(
                    new File(gmlFileName), new File(dtlFileName), new File(sqlFileName));

            System.out.printf("Query:\n%s", hd.toQuery());
        } catch (IOException e) {
            System.err.println("Error reading hypertree file: " + e.getMessage());
        }
    }
}
