package hypergraph;

import java.util.HashSet;
import java.util.Set;

public class Hyperedge {
    private String name;
    private Set<String> nodes;

    public Hyperedge() {
        nodes = new HashSet<>();
    }

    public Hyperedge(String name, Set<String> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "hypergraph.Hyperedge{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
