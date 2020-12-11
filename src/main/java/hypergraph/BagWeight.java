package hypergraph;

import java.util.Set;

public class BagWeight {
    private Set<Hyperedge> bag;
    private Double weight;

    public BagWeight(Set<Hyperedge> bag, Double weight) {
        this.bag = bag;
        this.weight = weight;
    }

    public Set<Hyperedge> getBag() {
        return bag;
    }

    public void setBag(Set<Hyperedge> bag) {
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
