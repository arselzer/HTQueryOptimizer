package hypergraph;

import java.util.Map;
import java.util.Set;

public class WeightedHypergraph extends Hypergraph {
    private Map<Hyperedge, Double> weights;

    public WeightedHypergraph() {
        super();
    }

    public WeightedHypergraph(Set<String> nodes, Set<Hyperedge> edges, Map<Hyperedge, Double> weights) {
        super(nodes, edges);
        this.weights = weights;
    }

    public Map<Hyperedge, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Hyperedge, Double> weights) {
        this.weights = weights;
    }
}
