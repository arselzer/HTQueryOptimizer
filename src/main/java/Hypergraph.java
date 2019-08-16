import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hypergraph<T extends Comparable> {
    Set<Hyperedge<T>> edges = new HashSet<>();
    Set<T> nodes = new HashSet<>();

    public Hypergraph() {

    }

    public Set<Hyperedge<T>> getEdges() {
        return edges;
    }

    public void setEdges(Set<Hyperedge<T>> edges) {
        this.edges = edges;
    }

    public void addEdge(Hyperedge<T> edge) {
        this.edges.add(edge);
    }

    public Set<T> getNodes() {
        return nodes;
    }

    public void setNodes(Set<T> nodes) {
        this.nodes = nodes;
    }

    public void addNode(T node) {
        this.nodes.add(node);
    }

    @Override
    public String toString() {
        return "Hypergraph{" +
                "edges=" + edges +
                ", nodes=" + nodes +
                '}';
    }
}
