package hypergraph;

import java.util.List;
import java.util.Objects;

public class HypertreeNode {
    String id;
    List<String> hyperedges;
    List<String> attributes;

    public HypertreeNode(String id, List<String> hyperedges, List<String> attributes) {
        this.id = id;
        this.hyperedges = hyperedges;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getHyperedges() {
        return hyperedges;
    }

    public void setHyperedges(List<String> hyperedges) {
        this.hyperedges = hyperedges;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "HypertreeNode{" +
                "id='" + id + '\'' +
                ", hyperedges=" + hyperedges +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
       HypertreeNode that = (HypertreeNode) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}