package query;

import java.util.List;
import java.util.LinkedList;

public class JoinTree {
    JoinTreeNode root = null;
    List<JoinTreeNode> leaves = new LinkedList<>();

    public JoinTree(JoinTreeNode root) {
        this.root = root;
    }

    public JoinTreeNode getRoot() {
        return root;
    }

    public void setRoot(JoinTreeNode root) {
        this.root = root;
    }

    public List<JoinTreeNode> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<JoinTreeNode> leaves) {
        this.leaves = leaves;
    }
}
