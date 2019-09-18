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
    private static double MAX_X = 10.0;
    private static double MAX_Y = 10.0;
    private static double MIN_NODE_DISTANCE = 1.0;
    private static double NEW_POINT_RADIUS = 6;
    private static int MAX_POINT_TRIES = 100;
    Set<String> nodeNames = new HashSet<>();
    ;
    Set<Node2D> nodes = new HashSet<>();
    HashMap<String, Node2D> nodesByName = new HashMap<>();
    List<Edge2D> edges = new LinkedList<>();
    private Hypergraph hg;

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
                    while (!conditionsMet && tries < MAX_POINT_TRIES) {
                        if (newEdgeNodes.isEmpty()) {
                            newNode = new Node2D(nodeName, Math.random() * MAX_X, Math.random() * MAX_Y);
                        } else {
                            Node2D lastNode = newEdgeNodes.get(newEdgeNodes.size() - 1);
                            double minX = Math.max(0, lastNode.getX() - NEW_POINT_RADIUS);
                            double maxX = Math.min(MAX_X, lastNode.getX() + NEW_POINT_RADIUS);
                            double minY = Math.max(0, lastNode.getY() - NEW_POINT_RADIUS);
                            double maxY = Math.min(MAX_Y, lastNode.getY() + NEW_POINT_RADIUS);
                            newNode = new Node2D(nodeName, minX + (Math.random() * 0.75 + 0.25) * (maxX - minX),
                                    minY + (Math.random() * 0.75 + 0.25) * (maxY - minY));
                        }

                        double minNodeDistance = Double.MAX_VALUE;

                        // Don't generate points really close by (readability)

                        for (Node2D node2D : nodes) {
                            if (newNode.distanceTo(node2D) < minNodeDistance) {
                                minNodeDistance = newNode.distanceTo(node2D);
                            }
                        }

                        // Either the min distance should be fulfilled or the number of tries exhausted
                        if (minNodeDistance > MIN_NODE_DISTANCE) {
                            conditionsMet = true;

                            // If the edge is a binary edge and the second point was not assigned yet
                            if (he.getNodes().size() == 2 && newEdgeNodes.size() == 1) {
                                //System.out.println("binary edges " + newEdge.getNodes().get(0) + " " + newNode);
                                double Ax = newEdge.getNodes().get(0).getX();
                                double Ay = newEdge.getNodes().get(0).getY();
                                double Bx = newNode.getX();
                                double By = newNode.getY();

                                boolean intersection = false;

                                for (Edge2D otherEdge : edges) {
                                    // For each other other binary edge
                                    if (otherEdge.getNodes().size() == 2 && !otherEdge.equals(newEdge)) {
                                        double Cx = otherEdge.getNodes().get(0).getX();
                                        double Cy = otherEdge.getNodes().get(0).getY();
                                        double Dx = otherEdge.getNodes().get(1).getX();
                                        double Dy = otherEdge.getNodes().get(1).getY();

                                        if (linesIntersect(Ax, Ay, Bx, By, Cx, Cy, Dx, Dy)) {
                                            intersection = true;
                                        }
                                    }
                                }

                                if (intersection) {
                                    //System.out.println("intersection" + tries);;
                                    conditionsMet = false;
                                }
                            }
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
            if (edge.getNodes().size() >= 2) {
                List<Node2D> convexHull = edge.getConvexHull();
                // Perform set difference to find out which points need to be checked
                Set<Node2D> nodesNotContainedInEdge = new HashSet<>(nodes);
                nodesNotContainedInEdge.removeAll(edge.getNodes());

                for (Node2D nodeToCheck : nodesNotContainedInEdge) {

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

                            double centerX = (n1.getX() + n2.getX()) / 2;
                            double centerY = (n1.getY() + n2.getY()) / 2;

                            // Length of line segment
                            double length = Math.sqrt(Math.pow(n2.getX() - n1.getX(), 2) + Math.pow(n2.getY() - n1.getY(), 2));
                            // Distance from center of line segment to node
                            double distance = Math.sqrt(Math.pow(centerX - nodeToCheck.getX(), 2) + Math.pow(centerY - nodeToCheck.getY(), 2));

                            // Only consider points "close" (within length of line segment to center)
                            if (distance < length) {
                                // The closer a point is, the worse
                                double closenessFactor = -500 / Math.pow((cp + 0.1), 2);//- 200 / Math.pow(0.1+cp, 2);
                                //System.out.println(cp + " " + closenessFactor);
                                rating += closenessFactor;
                            }
                        }
                    }

                    if (onTheRightOfAllNodes) {
                        // Discard the graph
                        rating -= 1000000;
                    }
                }

                if (edge.getNodes().size() >= 3) {
                    double maxDistance = -Double.MAX_VALUE;
                    double minDistance = Double.MAX_VALUE;
                    double minAngle = Math.PI;

                    for (int i = 0; i < convexHull.size() - 2; i++) {
                        Node2D node = convexHull.get(i);
                        Node2D next = convexHull.get(i + 1);

                        Node2D prevNode;
                        if (i == 0) {
                            prevNode = convexHull.get(convexHull.size() - 2);
                        } else {
                            prevNode = convexHull.get((i - 1));
                        }

                        double distance = Math.sqrt(Math.pow(next.getX() - node.getX(), 2) + Math.pow(next.getY() - node.getY(), 2));

                        if (distance > maxDistance) {
                            maxDistance = distance;
                        }
                        if (distance < minDistance) {
                            minDistance = distance;
                        }

                        // Vector prevNode -> node
                        double x1 = node.getX() - prevNode.getX();
                        double y1 = node.getY() - prevNode.getY();
                        // Vector nextNode -> node
                        double x2 = node.getX() - next.getX(); //nextNode.getX() - node.getX();
                        double y2 = node.getY() - next.getY(); //nextNode.getY() - node.getY();

                        double length1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
                        double length2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
                        double dotProduct = x1 * x2 + y1 * y2;

                        double angle = Math.acos(dotProduct / (length1 * length2));//Math.atan2(y2, x2) - Math.atan2(y1, x1);

                        if (angle < minAngle) {
                            minAngle = angle;
                        }
                    }

                    double distanceRatio = maxDistance / minDistance;
                    rating -= Math.pow(distanceRatio - 1, 3);

                    rating -= 30 / Math.pow(minAngle + 0.01, 3);

                    double minX = Double.MAX_VALUE;
                    double maxX = -Double.MAX_VALUE;
                    double minY = Double.MAX_VALUE;
                    double maxY = -Double.MAX_VALUE;

                    for (Node2D node : edge.getNodes()) {
                        if (node.getX() < minX) {
                            minX = node.getX();
                        }
                        if (node.getX() > maxX) {
                            maxX = node.getX();
                        }
                        if (node.getY() < minY) {
                            minY = node.getY();
                        }
                        if (node.getY() > maxY) {
                            maxY = node.getY();
                        }
                    }

                    double width = maxX - minX;
                    double height = maxY - minY;

                    double min = Math.min(width, height);
                    double max = Math.max(width, height);
                    double ratio = max / min;

                    // Penalize thin edges
                    rating -= Math.pow(ratio - 1, 5);
                }
            }
        }

        return rating;
    }

    // Taken from here: https://stackoverflow.com/questions/3838329/how-can-i-check-if-two-segments-intersect
    private boolean ccw(double Ax, double Ay, double Bx, double By, double Cx, double Cy) {
        return (Cy - Ay) * (Bx - Ax) > (By - Ay) * (Cx - Ax);
    }

    private boolean linesIntersect(double Ax, double Ay, double Bx, double By,
                                   double Cx, double Cy, double Dx, double Dy) {
        return (ccw(Ax, Ay, Cx, Cy, Dx, Dy) != ccw(Bx, By, Cx, Cy, Dx, Dy)) &&
                (ccw(Ax, Ay, Bx, By, Cx, Cy) != ccw(Ax, Ay, Bx, By, Dx, Dy));
    }

    public String toLaTeX() {
        String output = "\\documentclass{standalone}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage{xcolor}\n" +
                "\\usetikzlibrary{topaths,calc}\n";

        int colorIndex = 0;
        for (Edge2D e : edges) {
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
                Node2D prevNode;
                if (i == 0) {
                    prevNode = convexHull.get(convexHull.size() - 2);
                } else {
                    prevNode = convexHull.get((i - 1));
                }

                Node2D nextNode = convexHull.get((i + 1) % convexHull.size());

                // Vector prevNode -> node
                double x1 = node.getX() - prevNode.getX();
                double y1 = node.getY() - prevNode.getY();
                // Vector nextNode -> node
                double x2 = node.getX() - nextNode.getX(); //nextNode.getX() - node.getX();
                double y2 = node.getY() - nextNode.getY(); //nextNode.getY() - node.getY();

                double length1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
                double length2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
                // Normalize vectors
                double middleVectorX = (x1 / length1) + (x2 / length2);
                double middleVectorY = (y1 / length1) + (y2 / length2);
                double middleVectorLength = Math.sqrt(Math.pow(middleVectorX, 2) + Math.pow(middleVectorY, 2));
                double middleVectorNormalX = middleVectorX / middleVectorLength;
                double middleVectorNormalY = middleVectorY / middleVectorLength;

                double factor = 0.35;

                drawCoords.add(String.format("(%.2f, %.2f)", node.getX() + middleVectorNormalX * factor, node.getY() + middleVectorNormalY * factor));

//                Node2D node = convexHull.get(i);
//
//                double diffX = node.getX() - edge.getCenterX();
//                double diffY = node.getY() - edge.getCenterY();
//                double euclidDistance = Math.sqrt(diffX * diffX + diffY * diffY);
//                double factor = 0.3;
//                double normalDiffX = diffX / euclidDistance;
//                double normalDiffY = diffY / euclidDistance;
//
//                drawCoords.add(String.format("(%.2f, %.2f)", node.getX() + normalDiffX * factor, node.getY() + normalDiffY * factor));
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
        pw.close();

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
