import hypergraph.Hyperedge;
import hypergraph.Hypergraph;
import org.junit.jupiter.api.Test;
import queryexecutor.ViewQueryExecutor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class HypergraphVisualizerTest {
    @Test
    void toLaTeX() {
        Hypergraph hg = new Hypergraph();
        hg.setNodes(Set.of("a", "b", "c", "d", "e", "f", "g", "h"));
        hg.setEdges(Set.of(new Hyperedge("e1", Set.of("a", "b", "c")),
                new Hyperedge("e2", Set.of("c", "d", "e")),
                new Hyperedge("e3", Set.of("f", "g")),
                new Hyperedge("e4", Set.of("b", "g", "e", "a"))));

        System.out.println(hg.toLaTeX());
    }

    @Test
    void toFile() throws IOException, InterruptedException {
        Hypergraph hg = new Hypergraph();
        hg.setNodes(Set.of("a", "b", "c", "d", "e", "f", "g", "h"));
        hg.setEdges(Set.of(new Hyperedge("e1", Set.of("a", "b", "c")),
                new Hyperedge("e2", Set.of("c", "d", "e")),
                new Hyperedge("e3", Set.of("f", "g")),
                new Hyperedge("e4", Set.of("b", "g", "e", "a"))));

        Path output = hg.toPDF();

        ProcessBuilder pb = new ProcessBuilder("evince", output.toAbsolutePath().toString());
        pb.start();
    }

    @Test
    void displayLargeGraph() throws IOException, InterruptedException {
        Hypergraph hg = new Hypergraph();
        hg.setNodes(Set.of("a", "b", "c", "d", "e", "f", "g", "h"));
        hg.setEdges(Set.of(new Hyperedge("e1", Set.of("a", "b")),
                new Hyperedge("e2", Set.of("c", "d")),
                new Hyperedge("e3", Set.of("f", "g")),
                new Hyperedge("e4", Set.of("g", "e", "a")),
                new Hyperedge("e5", Set.of("c", "h")),
                new Hyperedge("e6", Set.of("g", "a")),
                new Hyperedge("e7", Set.of("f", "b")),
                new Hyperedge("e8", Set.of("g", "c")),
                new Hyperedge("e9", Set.of("g", "e", "d"))));

        hg.displayPDF();
    }
}
