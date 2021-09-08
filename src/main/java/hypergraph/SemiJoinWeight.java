package hypergraph;

import java.util.List;
import java.util.Set;

public class SemiJoinWeight {
    private List<Hyperedge> bag;
    private Double weight;

    public SemiJoinWeight(List<Hyperedge> bag, Double weight) {
        this.bag = bag;
        this.weight = weight;
    }

    public List<Hyperedge> getBag() {
        return bag;
    }

    public void setBag(List<Hyperedge> bag) {
        this.bag = bag;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "BagWeight{" +
                "bag=" + bag +
                ", weight=" + weight +
                '}';
    }
}
