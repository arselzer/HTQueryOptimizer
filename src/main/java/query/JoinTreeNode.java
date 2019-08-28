package query;

import java.util.LinkedList;
import java.util.List;

public class JoinTreeNode {
    private List<JoinTreeNode> successors = new LinkedList<>();
    private JoinTreeNode predecessor = null;
    private List<String> tables = new LinkedList<>();
    private List<String> attributes = new LinkedList<>();

    public JoinTreeNode() {

    }

    public JoinTreeNode(JoinTreeNode predecessor) {
        this.predecessor = predecessor;
    }

    public List<JoinTreeNode> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<JoinTreeNode> successors) {
        this.successors = successors;
    }

    public JoinTreeNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(JoinTreeNode predecessor) {
        this.predecessor = predecessor;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "JoinTreeNode{" +
                "tables=" + tables +
                ", attributes=" + attributes +
                ", successors=" + successors +

                "}";
    }
}
