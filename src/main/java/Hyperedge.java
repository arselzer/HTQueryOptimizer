import java.util.HashSet;
import java.util.Set;

public class Hyperedge<T extends Comparable> {
    String name;
    Set<T> nodes;

    public Hyperedge() {
        nodes = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<T> getNodes() {
        return nodes;
    }

    public void setNodes(Set<T> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "Hyperedge{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
