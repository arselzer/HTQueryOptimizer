import hypergraph.Hyperedge;
import hypergraph.Hypergraph;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
}
