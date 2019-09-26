package hypergraph;

public class DecompositionOptions {
    public static enum DecompAlgorithm {
        DETKDECOMP,
        BALANCEDGO
    }

    private DecompAlgorithm algorithm;

    public DecompositionOptions(DecompAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public DecompositionOptions() {
        this.algorithm = DecompAlgorithm.DETKDECOMP;
    }

    public DecompAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(DecompAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
