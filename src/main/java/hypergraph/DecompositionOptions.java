package hypergraph;

public class DecompositionOptions {
    private DecompAlgorithm algorithm;

    public DecompositionOptions(DecompAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public DecompositionOptions() {
        this.algorithm = DecompAlgorithm.BALANCEDGO;
    }

    public DecompAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(DecompAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public enum DecompAlgorithm {
        DETKDECOMP,
        BALANCEDGO
    }
}
