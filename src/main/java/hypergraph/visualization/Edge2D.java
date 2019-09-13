package hypergraph.visualization;

import java.util.LinkedList;
import java.util.List;

class Edge2D {
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

            for (Node2D otherNode : nodes) {
                if (!otherNode.equals(currentNode)) {
                    double cp = crossProduct(currentNode.getX(), currentNode.getY(), nextNode.getX(), nextNode.getY(), otherNode.getX(), otherNode.getY());
                    // Check if the point is on the left side of the line from the current point to the next point
                    if (cp > 0) {
                        // Set next node and recompute angle to next node
                        nextNode = otherNode;
                    }
                }
            }

            convexHull.add(nextNode);
            currentNode = nextNode;
        }

        return convexHull;
    }

    /**
     * Computes the cross product
     * @return A number indicating whether point c is on the left side (> 0), right side (< 0) or exactly
     * on the line between a and b (= 0)
     */
    private static double crossProduct(double a_x, double a_y, double b_x, double b_y, double c_x, double c_y) {
        double y1 = a_y - b_y;
        double y2 = a_y - c_y;
        double x1 = a_x - b_x;
        double x2 = a_x - c_x;
        return y2 * x1 - y1 * x2;
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
