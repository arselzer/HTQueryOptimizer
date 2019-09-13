package hypergraph.visualization;

import hypergraph.Hypergraph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class HypergraphVisualizer {
    private Hypergraph hg;

    private static int NUMBER_INSTANCES = 1000;

    List<VisualizationInstance> candidates = new LinkedList<>();

    VisualizationInstance bestInstance = null;

    public HypergraphVisualizer(Hypergraph hg) {
        this.hg = hg;

        for (int i = 0; i < NUMBER_INSTANCES; i++) {
            VisualizationInstance candidate = new VisualizationInstance(hg);
            candidates.add(candidate);
        }

        // Double.MIN_VALUE is actually positive...
        double bestScore = -Double.MAX_VALUE;
        for (VisualizationInstance instance : candidates) {
            double score = instance.rate();
            if (score > bestScore) {
                bestScore = score;
                bestInstance = instance;
            }
        }
    }

    public String toLaTeX() {
        return bestInstance.toLaTeX();
    }

    public Path toPDF() throws IOException, InterruptedException {
        return bestInstance.toPDF();
    }

    public void toPDF(Path outputFile) throws IOException, InterruptedException {
        bestInstance.toPDF(outputFile);
    }

    public void displayPDF() throws IOException, InterruptedException {
        bestInstance.displayPDF();
    }

}
