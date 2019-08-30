package query;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class JoinTreeNode {
    private List<JoinTreeNode> successors = new LinkedList<>();
    private JoinTreeNode predecessor = null;
    private List<String> tables = new LinkedList<>();
    private List<String> attributes = new LinkedList<>();

    public JoinTreeNode() {

    }

    public Set<JoinTreeNode> getDeepestLeaves() {
        int height = getHeight();
        System.out.println("height: " + height);

        Set<JoinTreeNode> deepestLeaves = new HashSet<>();
        HashMap<JoinTreeNode, Integer> depthMap = new HashMap<>();
        Queue<JoinTreeNode> queue = new LinkedList<>();

        // Add direct ancestors with depth 2
        for (JoinTreeNode node : successors) {
            queue.add(node);
            depthMap.put(node, 2);
        }

        // Perform a search for the deepest leave nodes
        while (!queue.isEmpty()) {
            JoinTreeNode node = queue.poll();
            int depth = depthMap.get(node);
            if (depth == height) {
                deepestLeaves.add(node);
            }
            for (JoinTreeNode successor : node.successors) {
                queue.offer(successor);
                depthMap.put(successor, depth + 1);
            }
        }

        return deepestLeaves;
    }

    public int getHeight() {
        Set<Integer> heights = successors.stream().map(JoinTreeNode::getHeight).collect(Collectors.toSet());
        int max = 0;
        for (Integer height : heights) {
            if (height > max) {
                max = height;
            }
        }

        return max + 1;
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
        return toIndentedString();
    }


    public String toIndentedString() {
        return toIndentedString(0);
    }

    public String toIndentedString(int n) {
        String indentation = "";
        for (int i = 0; i < n; i++) {
            indentation += "  ";
        }
        String successorsString = successors
                .stream().map(node -> node.toIndentedString(n+1)).collect(Collectors.joining("\n"));
        if (!successorsString.isEmpty()) {
            successorsString = "\n" + successorsString;
        }
        return indentation + "JoinTreeNode{" +
                "tables=" + tables +
                ", attributes=" + attributes +
                ", successors=[" + successorsString +
                "]}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinTreeNode that = (JoinTreeNode) o;
        return Objects.equals(predecessor, that.predecessor) &&
                tables.equals(that.tables) &&
                attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predecessor, tables, attributes);
    }
}
