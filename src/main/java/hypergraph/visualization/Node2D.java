package hypergraph.visualization;

import java.util.Objects;

class Node2D {
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
