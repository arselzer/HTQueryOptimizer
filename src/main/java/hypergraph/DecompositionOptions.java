package hypergraph;

public class DecompositionOptions {
    private DecompAlgorithm algorithm;
    private boolean depthOpt = false;

    public DecompositionOptions(DecompAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public DecompositionOptions(DecompAlgorithm algorithm, boolean depthOpt) {
        this.algorithm = algorithm;
        this.depthOpt = depthOpt;
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

    public boolean isDepthOpt() {
        return depthOpt;
    }

    public void setDepthOpt(boolean depthOpt) {
        this.depthOpt = depthOpt;
    }
}
