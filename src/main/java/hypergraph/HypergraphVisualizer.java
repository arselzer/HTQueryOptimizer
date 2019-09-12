package hypergraph;

import java.util.*;

public class HypergraphVisualizer {
    private Hypergraph hg;

    private static double MAX_X = 10.0;
    private static double MAX_Y = 10.0;

    Set<String> nodeNames = new HashSet<>();
    Set<Node2D> nodes = new HashSet<>();
    HashMap<String, Node2D> nodesByName = new HashMap<>();
    List<Edge2D> edges = new LinkedList<>();

    public HypergraphVisualizer(Hypergraph hg) {
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
                }
                else {
                    newEdgeNodes.add(nodesByName.get(nodeName));
                }
            }
        }

        for (Edge2D edge : edges) {
            double sumX = 0;
            double sumY = 0;
            int count = 0;

            for (Node2D node : edge.getNodes()) {
                System.out.println(node);;
                sumX += node.getX();
                sumY += node.getY();
                count++;
            }

            edge.setCenterX(sumX / count);
            edge.setCenterY(sumY / count);
        }
    }

    private class Node2D {
        private String name;
        private double x;
        private double y;

        public Node2D(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public double distanceTo(Node2D other) {
            return Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node2D node2D = (Node2D) o;
            return Double.compare(node2D.x, x) == 0 &&
                    Double.compare(node2D.y, y) == 0 &&
                    Objects.equals(name, node2D.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, x, y);
        }

        @Override
        public String toString() {
            return "Node2D{" +
                    "name='" + name + '\'' +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private class Edge2D {
        private String name;
        private List<Node2D> nodes;
        private double centerX;
        private double centerY;

        private String color;

        public List<Node2D> getConvexHull() {
            if (nodes.size() == 1) {
                return nodes;
            }

            if (nodes.size() == 2) {
                return List.of(nodes.get(0), nodes.get(1), nodes.get(0));
            }

            LinkedList<Node2D> convexHull = new LinkedList<>();

            Node2D leftmost = nodes.get(0);
            double leftmostX = leftmost.getX();
            for (Node2D node : nodes) {
                if (node.getX() < leftmostX) {
                    leftmostX = node.getX();
                    leftmost = node;
                }
            }
            // Determine the leftmost node as it has to be on the convex hull
            convexHull.add(leftmost);

            Node2D currentNode = convexHull.get(0);

            // Check if the convex hull is greater than 1 in case the first node is the leftmost
            while (convexHull.size() < 2 || !convexHull.get(0).equals(convexHull.get(convexHull.size()-1))) {
                // Choose any *other* node as the next node (0 or 1 have to be different)
                Node2D nextNode = nodes.get(0);
                if (currentNode.equals(nextNode)) {
                    nextNode = nodes.get(1);
                }

                double angleToNext = Math.atan2(nextNode.getY() - currentNode.getY(), nextNode.getX() - currentNode.getX());

                for (Node2D otherNode : nodes) {
                    if (!otherNode.equals(currentNode)) {
                        double angleToOther = Math.atan2(otherNode.getY() - currentNode.getY(), otherNode.getX() - currentNode.getX());
                        // If the angle from the current node to the picked node is larger than to the current next, pick it
                        // as the next node instead
                        //System.out.println(currentNode + " angle to node " + otherNode + ": " + angleToOther);
                        double angleDifference = angleToOther - angleToNext;
                        if (angleDifference > 0 && angleDifference < Math.PI) {
                            // Set next node and recompute angle to next node
                            nextNode = otherNode;
                            angleToNext = Math.atan2(nextNode.getY() - currentNode.getY(), nextNode.getX() - currentNode.getX());
                        }
                    }
                }

                convexHull.add(nextNode);
                currentNode = nextNode;
            }

            return convexHull;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Node2D> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node2D> nodes) {
            this.nodes = nodes;
        }

        public double getCenterX() {
            return centerX;
        }

        public void setCenterX(double centerX) {
            this.centerX = centerX;
        }

        public double getCenterY() {
            return centerY;
        }

        public void setCenterY(double centerY) {
            this.centerY = centerY;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return "Edge2D{" +
                    "name='" + name + '\'' +
                    ", nodes=" + nodes +
                    ", centerX=" + centerX +
                    ", centerY=" + centerY +
                    ", color='" + color + '\'' +
                    '}';
        }
    }

    public String toLaTeX() {
        String output = "\\documentclass{article}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage{xcolor}\n" +
                "\\usetikzlibrary{topaths,calc}\n";

        int colorIndex = 0;
        for (Edge2D e: edges) {
            // Seed rng to get the same colors
            Random rand = new Random(e.getName().hashCode());
            rand.nextDouble();

            String colorName = "c" + colorIndex;
            output += String.format("\\definecolor{%s}{rgb}{%.2f,%.2f,%.2f}\n", colorName,
                    rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
            colorIndex++;

            e.setColor(colorName);
        }

        output += "\\begin{document}\n";

        output += "\\begin{tikzpicture}\n";

        for (Node2D node : nodes) {
            output += String.format("\\node (%s) at (%.2f,%.2f) {};\n", node.getName(), node.getX(), node.getY());
        }

        output += "\\begin{scope}[fill opacity=0.8]\n";

        for (Edge2D edge : edges) {
            LinkedList<String> drawCoords = new LinkedList<>();
            if (edge.getNodes().size() >= 2) {
                List<Node2D> convexHull = edge.getConvexHull();

                for (Node2D node : convexHull) {
                    double diffX = node.getX() - edge.getCenterX();
                    double diffY = node.getY() - edge.getCenterY();
                    double euclidDistance = Math.sqrt(diffX * diffX + diffY * diffY);
                    double normalDiffX = diffX / euclidDistance;
                    double normalDiffY = diffY / euclidDistance;

                    drawCoords.add(String.format("(%.2f, %.2f)", node.getX() + normalDiffX, node.getY() + normalDiffY));
                }
            }
            output += String.format("\\draw[fill=%s] plot [smooth] %s;\n", edge.getColor(), String.join(" -- ", drawCoords));
        }

        output += "\\end{scope}\n";

        output += String.format("\\foreach \\v in {%s} {\n", String.join(",", nodeNames));
        output += "  \\fill (\\v) circle (0.1);\n";
        output += "}\n";

        output += "\\end{tikzpicture}\n";
        output += "\\end{document}\n";

        return output;
    }
}
