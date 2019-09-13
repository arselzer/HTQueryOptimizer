package hypergraph.visualization;

import hypergraph.Hyperedge;
import hypergraph.Hypergraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VisualizationInstance {
    private Hypergraph hg;

    private static double MAX_X = 10.0;
    private static double MAX_Y = 10.0;

    Set<String> nodeNames = new HashSet<>();
    Set<Node2D> nodes = new HashSet<>();
    HashMap<String, Node2D> nodesByName = new HashMap<>();
    List<Edge2D> edges = new LinkedList<>();

    public VisualizationInstance(Hypergraph hg) {
        this.hg = hg;

        for (Hyperedge he : hg.getEdges()) {
            Edge2D newEdge = new Edge2D();
            newEdge.setName(he.getName());
            List<Node2D> newEdgeNodes = new LinkedList<>();
            newEdge.setNodes(newEdgeNodes);
            edges.add(newEdge);

            for (String nodeName : he.getNodes()) {
                // Check if the node was already drawn
                if (!nodeNames.contains(nodeName)) {
                    boolean conditionsMet = false;

                    Node2D newNode = null;

                    int tries = 0;
                    while (!conditionsMet) {
                        newNode = new Node2D(nodeName, Math.random() * MAX_X, Math.random() * MAX_Y);

                        double minNodeDistance = Double.MAX_VALUE;

                        for (Node2D node2D : nodes) {
                            if (newNode.distanceTo(node2D) < minNodeDistance) {
                                minNodeDistance = newNode.distanceTo(node2D);
                            }
                        }

                        // Either the min distance should be fulfilled or the number of tries exhausted
                        if (minNodeDistance > 1.0 || tries > 10) {
                            conditionsMet = true;
                        }

                        tries++;
                    }

                    newEdgeNodes.add(newNode);
                    nodeNames.add(nodeName);
                    nodes.add(newNode);
                    nodesByName.put(newNode.getName(), newNode);
                } else {
                    newEdgeNodes.add(nodesByName.get(nodeName));
                }
            }
        }

        for (Edge2D edge : edges) {
            double sumX = 0;
            double sumY = 0;
            int count = 0;

            for (Node2D node : edge.getNodes()) {
                sumX += node.getX();
                sumY += node.getY();
                count++;
            }

            edge.setCenterX(sumX / count);
            edge.setCenterY(sumY / count);
        }
    }

    public double rate() {
        // Start with 0 as the base. Add or subtract based on good/bad properties
        double rating = 0.0;

        for (Edge2D edge : edges) {
            List<Node2D> convexHull = edge.getConvexHull();
            // Perform set difference to find out which points need to be checked
            Set<Node2D> nodesNotContainedInEdge = new HashSet<>(nodes);
            nodesNotContainedInEdge.removeAll(edge.getNodes());

            for (Node2D nodeToCheck: nodesNotContainedInEdge) {

                // Check for each point that is not supposed to be inside the convex hull whether it is inside
                // If the point is on the right of all vectors of the hull it is contained
                boolean onTheRightOfAllNodes = true;
                for (int i = 0; i < convexHull.size() - 1; i++) {
                    Node2D n1 = convexHull.get(i);
                    Node2D n2 = convexHull.get(i + 1); // Wrap around one element (last-first)

                    double cp = Edge2D.crossProduct(n1.getX(), n1.getY(), n2.getX(), n2.getY(), nodeToCheck.getX(), nodeToCheck.getY());

                    // Check if the node is on the left side
                    if (cp > 0) {
                        onTheRightOfAllNodes = false;
                        // If it is on the left side of any node, stop
                        break;
                    }
                    else {
                        // The closer a point is, the worse
                        double closenessFactor = - 500 / Math.pow(-(cp+0.1), 2);//- 200 / Math.pow(0.1+cp, 2);
                        //System.out.println(cp + " " + closenessFactor);
                        rating += closenessFactor;
                    }
                }

                if (onTheRightOfAllNodes) {
                    // Discard the graph
                    rating -= 1000000;
                }
            }
        }

        return rating;
    }

    public String toLaTeX() {
        String output = "\\documentclass{standalone}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage{xcolor}\n" +
                "\\usetikzlibrary{topaths,calc}\n";

        int colorIndex = 0;
        for (Edge2D e: edges) {
            // Seed rng to get the same colors
            Random rand = new Random(e.getName().hashCode());
            rand.nextDouble();

            String colorName = "c" + colorIndex;
            // Scale color components to range [.4,1] for lighter colors
            output += String.format("\\definecolor{%s}{rgb}{%.2f,%.2f,%.2f}\n", colorName,
                    0.4 + rand.nextDouble() * 0.6,
                    0.4 + rand.nextDouble() * 0.6,
                    0.4 + rand.nextDouble() * 0.6);
            colorIndex++;

            e.setColor(colorName);
        }

        output += "\\begin{document}\n";

        output += "\\begin{tikzpicture}\n";

        for (Node2D node : nodes) {
            output += String.format("\\node (%s) at (%.2f,%.2f) {};\n", node.getName(), node.getX(), node.getY());
        }

        output += "\\begin{scope}[fill opacity=0.7]\n";

        for (Edge2D edge : edges) {
            LinkedList<String> drawCoords = new LinkedList<>();

            List<Node2D> convexHull = edge.getConvexHull();

            // Ignore the last element, which is the first element
            for (int i = 0; i < convexHull.size() - 1; i++) {
                Node2D node = convexHull.get(i);

                double diffX = node.getX() - edge.getCenterX();
                double diffY = node.getY() - edge.getCenterY();
                double euclidDistance = Math.sqrt(diffX * diffX + diffY * diffY);
                double factor = 0.3;
                double normalDiffX = diffX / euclidDistance;
                double normalDiffY = diffY / euclidDistance;

                drawCoords.add(String.format("(%.2f, %.2f)", node.getX() + normalDiffX * factor, node.getY() + normalDiffY * factor));
            }
            if (edge.getNodes().size() > 2) {
                output += String.format("\\draw[fill=%s] plot [smooth cycle] coordinates {%s};\n", edge.getColor(), String.join(" ", drawCoords));
            } else if (edge.getNodes().size() == 2) {
                Node2D n1 = convexHull.get(0);
                Node2D n2 = convexHull.get(1);
                output += String.format("\\draw[%s, line width=0.5mm] (%.2f,%.2f) -- (%.2f, %.2f);\n", edge.getColor(), n1.getX(), n1.getY(), n2.getX(), n2.getY());
            }
        }

        output += "\\end{scope}\n";

        output += String.format("\\foreach \\v in {%s} {\n", String.join(",", nodeNames));
        output += "  \\fill (\\v) circle (0.1);\n";
        output += "}\n";

        for (Node2D node : nodes) {
            output += String.format("\\fill[white] (%.2f, %.2f) circle (0.12);\n", node.getX(), node.getY());
            output += String.format("\\fill (%s) circle (0.1);\n", node.getName(), node.getName());

            //output += String.format("\\fill[white] (%.2f, %.2f) circle (0.25);\n", node.getX() + 0.3, node.getY() - 0.3);
            output += String.format("\\node at (%.2f, %.2f) {$%s$};\n", node.getX() + 0.3, node.getY() - 0.3, node.getName());
        }

        for (Edge2D edge : edges) {
            output += String.format("\\draw[fill=%s, opacity=0.85] (%.2f, %.2f) circle (0.3);\n", edge.getColor(), edge.getCenterX(), edge.getCenterY());
            output += String.format("\\node at (%.2f, %.2f) {$%s$};\n", edge.getCenterX(), edge.getCenterY(), edge.getName());
        }

        output += "\\end{tikzpicture}\n";
        output += "\\end{document}\n";

        return output;
    }

    public Path toPDF() throws IOException, InterruptedException {
        File latexSourceFile = File.createTempFile("hypergraph", ".tex");
        latexSourceFile.deleteOnExit();
        String latexContent = toLaTeX();

        PrintWriter pw = new PrintWriter(latexSourceFile);
        pw.write(latexContent);
        pw.flush();

        Path tempDir = Files.createTempDirectory("hypergraph-latex-output");

        ProcessBuilder pb = new ProcessBuilder("pdflatex", "-output-format=pdf",
                "-output-directory=" + tempDir.toAbsolutePath().toString(),
                latexSourceFile.getAbsolutePath());
        Process process = pb.start();
        process.waitFor(2, TimeUnit.SECONDS);

        Path src = Paths.get(tempDir.toAbsolutePath().toString() + "/" +
                latexSourceFile.getName().toString().replace(".tex", ".pdf"));

        return src;
    }

    public void toPDF(Path outputFile) throws IOException, InterruptedException {
        Path src = toPDF();
        Path dst = outputFile;

        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    public void displayPDF() throws IOException, InterruptedException {
        Path output = toPDF();
        ProcessBuilder pb = new ProcessBuilder("evince", output.toAbsolutePath().toString());
        pb.start();
    }

    public Set<String> getNodeNames() {
        return nodeNames;
    }

    public void setNodeNames(Set<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    public Set<Node2D> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node2D> nodes) {
        this.nodes = nodes;
    }

    public HashMap<String, Node2D> getNodesByName() {
        return nodesByName;
    }

    public void setNodesByName(HashMap<String, Node2D> nodesByName) {
        this.nodesByName = nodesByName;
    }

    public List<Edge2D> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge2D> edges) {
        this.edges = edges;
    }
}
