import hypergraph.Hypergraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        String dtlFileName = args[0];

        String hypergraphFile = null;
        try {
            hypergraphFile = Files.lines(Path.of(args[0])).collect(Collectors.joining());
            Hypergraph hg = Hypergraph.fromDTL(hypergraphFile);

            hg.toPDF(Paths.get("hypergraph.pdf"));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getJarName() {
        return new File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}
