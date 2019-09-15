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
                new Hyperedge("e5", Set.of("g")),
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

    @Test
    void displayComplexBinaryGraph() throws IOException, InterruptedException {
        Hypergraph hg = Hypergraph.fromDTL("t11(X23,X18),\n" +
                "t14(X38,X39),\n" +
                "t9(X23,X28),\n" +
                "t18(X9,X3),\n" +
                "t1(X14,X15),\n" +
                "t8(X25,X28),\n" +
                "t5(X1,X5),\n" +
                "t19(X2,X3),\n" +
                "t10(X23,X22),\n" +
                "t6(X33,X5),\n" +
                "t12(X9,X18),\n" +
                "t20(X9,X3),\n" +
                "t13(X9,X38),\n" +
                "t3(X11,X14),\n" +
                "t2(X14,X7),\n" +
                "t15(X38,X26),\n" +
                "t16(X9,X31),\n" +
                "t17(X8,X9),\n" +
                "t7(X14,X25),\n" +
                "t4(X1,X14).");

        hg.displayPDF();
    }
}
