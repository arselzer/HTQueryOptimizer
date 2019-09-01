

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.printf("Usage: java -jar %s {hypertree} {hypergraph} {query} {schema}", getJarName());
        }

        String gmlFileName = args[0];
        String dtlFileName = args[1];
        String sqlFileName = args[2];
        String schemaFileName = args[3];


    }

    public static String getJarName() {
        return new File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
