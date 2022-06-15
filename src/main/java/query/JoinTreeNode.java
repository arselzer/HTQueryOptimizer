package query;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import exceptions.JoinTreeGenerationException;
import hypergraph.Hypergraph;
import net.sf.jsqlparser.statement.select.Join;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class JoinTreeNode {
    @SerializedName("children")
    private List<JoinTreeNode> successors = new LinkedList<>();
    // Predecessor is marked transient to prevent cycles when serializing
    private transient JoinTreeNode predecessor = null;
    private List<String> tables = new LinkedList<>();
    private List<String> attributes = new LinkedList<>();

    public JoinTreeNode() {

    }

    public JoinTreeNode(JoinTreeNode predecessor) {
        this.predecessor = predecessor;
    }

    public String getIdentifier() {
        //return "htqo_tbl_" + UUID.fromString(String.join("", tables)).toString().replace("-","");
        return "htqo_" + String.join("_", tables);
        // UUID or more readable form?
    }

    public String getIdentifier(int stage) {
        return "htqo_stage_" + stage + "_" + String.join("_", tables);
    }

    /**
     * Removes all unnecessary intermediate columns such that the amount of data stored is reduced to
     * the minimum needed
     *
     * @param projectColumns columns that are needed for the end result of the query
     */
    public void projectAllColumns(Collection<String> projectColumns, Hypergraph hg) {
        for (Set<JoinTreeNode> layer : getLayers()) {
            for (JoinTreeNode node : layer) {
                HashSet<String> remainingAttributes = new HashSet<>(node.getAttributes());

                HashSet<String> toKeep = new HashSet<>(projectColumns);
                // Add all attributes occurring in predecessor nodes
                if (node.getPredecessor() != null) {
                    for (String table : node.getTables()) {
                        for (String predecessorTable : node.getPredecessor().getTables()) {
                            if (!table.equals(predecessorTable)) {
                                Set<String> semiJoinAttributes = new HashSet<>(hg.getEdgeByName(table).getNodes());
                                semiJoinAttributes.retainAll(hg.getEdgeByName(predecessorTable).getNodes());
                                toKeep.addAll(semiJoinAttributes);
                            }
                        }
                    }
                }
                // Add all attributes occuring in successor nodes
                for (JoinTreeNode successor : node.getSuccessors()) {
                    for (String table : node.getTables()) {
                        for (String predecessorTable : successor.getTables()) {
                            if (!table.equals(predecessorTable)) {
                                Set<String> semiJoinAttributes = new HashSet<>(hg.getEdgeByName(table).getNodes());
                                semiJoinAttributes.retainAll(hg.getEdgeByName(predecessorTable).getNodes());
                                toKeep.addAll(semiJoinAttributes);
                            }
                        }
                    }
                }
                // Add all attributes needed for in-node joins
                Set<String> joinAttributes = new HashSet<>(hg.getEdgeByName(node.getTables().get(0)).getNodes());
                for (int i = 1; i < node.getTables().size(); i++) {
                    Set<String> newAttributes = new HashSet<>(hg.getEdgeByName(node.getTables().get(i)).getNodes());
                    for (String attr: newAttributes) {
                        if (joinAttributes.contains(attr)) {
                            toKeep.add(attr);
                        }
                    }
                    joinAttributes.addAll(newAttributes);
                }

                remainingAttributes.retainAll(toKeep);
                node.setAttributes(new LinkedList<>(remainingAttributes));
            }
        }
    }

    public List<Set<JoinTreeNode>> getLayers() {
        int height = getHeight();
        System.out.println(height);
        List<Set<JoinTreeNode>> layers = new ArrayList<>(height);

        for (int i = 0; i < height; i++) {
            layers.add(new HashSet<>());
        }

        layers.get(height - 1).add(this);

        Queue<JoinTreeNode> queue = new LinkedList<>(this.getSuccessors());

        // Perform a search for the deepest leave nodes
        while (!queue.isEmpty()) {
            JoinTreeNode node = queue.poll();
            int nodeHeight = node.getHeight();

            layers.get(nodeHeight - 1).add(node);

            for (JoinTreeNode successor : node.successors) {
                queue.offer(successor);
            }
        }

        Collections.reverse(layers);
        return layers;
    }

    /**
     * @return the tree converted into layers of equal depth from 0 to depth
     */
    public List<Set<JoinTreeNode>> _getLayers() {
        LinkedList<Set<JoinTreeNode>> layers = new LinkedList<>();
        layers.add(Set.of(this));
        layers.add(new HashSet<>(successors));

        int currentLayerIndex = 1;

        // Perform a BFS to find all height layers of the tree
        while (layers.get(currentLayerIndex).size() > 0) {
            Set<JoinTreeNode> nextLayer = new HashSet<>();

            for (JoinTreeNode node : layers.get(currentLayerIndex)) {
                nextLayer.addAll(node.successors);
            }

            layers.add(nextLayer);

            currentLayerIndex++;
        }

        // Remove the last, empty layer
        layers.removeLast();

        return layers;
    }

    /**
     * @return All nodes of the join tree as a Set
     */
    public Set<JoinTreeNode> getAllNodes() {
        Set<JoinTreeNode> nodes = new HashSet<>();
        for (Set<JoinTreeNode> layer : getLayers()) {
            nodes.addAll(layer);
        }
        return nodes;
    }

    public List<List<JoinTreeNode>> getFinalJoinSplit() {
        List<List<JoinTreeNode>> joinSplit = new LinkedList<>();
        String json = toJSON();
        System.out.println("json: " + json);

        File jsonFile;
        try {
            jsonFile = new File("jointree.json");

            PrintWriter out = new PrintWriter(jsonFile);
            out.write(json);
            out.close();

            Process process = new ProcessBuilder("python3", "split.py", "-t", "10", "jointree.json").start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = br.lines().collect(Collectors.joining());
            // System.out.println(output);

            String jsonOutputString = Files.readString(Path.of("grouping.json"));

            Map<String, List> jsonOutput = (new Gson()).fromJson(jsonOutputString, Map.class);

            Set<JoinTreeNode> allNodes = getAllNodes();
            Map<String, JoinTreeNode> allNodesByTableName = new HashMap<>();
            for (JoinTreeNode node : allNodes) {
                allNodesByTableName.put(node.getTables().get(0), node);
            }
            jsonOutput.values().forEach(group -> {
                List<JoinTreeNode> joinGroup = new LinkedList<>();
                for (String tableName : (List<String>) group) {
                    joinGroup.add(allNodesByTableName.get(tableName));
                }
                joinSplit.add(joinGroup);
            });
        } catch (IOException e) {
            System.out.println("Error creating temp file: " + e);
        }

        return joinSplit;
    }

    /**
     * Quickly retrieves the leaf nodes of maximum depth (of the whole tree)
     *
     * @return Leaf nodes at max depth
     */
    public Set<JoinTreeNode> getDeepestLeaves() {
        int height = getHeight();

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

    public int getNumberOfNodes() {
        return 1 + successors.stream()
                .map(JoinTreeNode::getNumberOfNodes)
                .reduce(0, Integer::sum);
    }

    public int getHypertreeWidth() {
        int maxWidth = tables.size();
        for (JoinTreeNode descendant : successors) {
            if (descendant.getHypertreeWidth() > maxWidth) {
                maxWidth = descendant.getHypertreeWidth();
            }
        }

        return maxWidth;
    }

    public double[] getEdgeBagSizes() {
        return getAllNodes().stream()
                .map(node -> node.tables.size())
                .mapToDouble(x -> x)
                .toArray();
    }

    public double[] getVertexBagSizes() {
        return getAllNodes().stream()
                .map(node -> node.attributes.size())
                .mapToDouble(x -> x)
                .toArray();
    }

    public DescriptiveStatistics getEdgeBagSizeStatistics() {
        return new DescriptiveStatistics(getEdgeBagSizes());
    }

    public DescriptiveStatistics getVertexBagSizeStatistics() {
        return new DescriptiveStatistics(getVertexBagSizes());
    }

    public boolean isJoinNode() {
        return successors.size() > 1;
    }

    public boolean isLeaf() {
        return successors.size() == 0;
    }

    public double getBalancednessFactor() {
        double balancednessValuesSum = 0;
        double count = 0;

        for (JoinTreeNode node : getAllNodes()) {
            if (node.isJoinNode()) {
                int smallerTreeSize = Integer.MAX_VALUE;
                int largerTreeSize = 0;

                for (JoinTreeNode successor : successors) {
                    int successorTreeSize = successor.getNumberOfNodes();
                    if (successorTreeSize < smallerTreeSize) {
                        smallerTreeSize = successorTreeSize;
                    }
                    if (successorTreeSize > largerTreeSize) {
                        largerTreeSize = successorTreeSize;
                    }
                }

                balancednessValuesSum += ((double) smallerTreeSize) / ((double) largerTreeSize);
                count += 1;
            }
        }

        return balancednessValuesSum / count;
    }

    private JoinTreeNode createCopyOfSubtree(JoinTreeNode excludeSuccessor) {
        JoinTreeNode newRoot = new JoinTreeNode();
        newRoot.setTables(new LinkedList<>(getTables()));
        newRoot.setAttributes(new LinkedList<>(getAttributes()));
        List<JoinTreeNode> newSuccessors = new LinkedList<>();
        for (JoinTreeNode successor : getSuccessors()) {
            if (!successor.equals(excludeSuccessor)) {
                JoinTreeNode successorCopy = successor.createCopyOfSubtree(null);
                successorCopy.setPredecessor(newRoot);
                newSuccessors.add(successorCopy);
            }
        }
        newRoot.setSuccessors(newSuccessors);
        return newRoot;
    }

    public Set<JoinTreeNode> getRerootedTrees() {
        Set<JoinTreeNode> rerootedTrees = new HashSet<>();
        Set<JoinTreeNode> allNodes = getAllNodes();

        for (JoinTreeNode node : allNodes) {
            // Create a copy of the subtree of the current tree node
            JoinTreeNode newTree = node.createCopyOfSubtree(null);
            // Keep track of the *copy* of the current subtree, i.e., its predecessors
            JoinTreeNode currentNewSubtree = newTree;
            // Keep track of the current predecessor to exclude it from the subtree
            JoinTreeNode currentNode = node;
            JoinTreeNode currentPredecessor = currentNode.getPredecessor();

            while (currentPredecessor != null) {
                JoinTreeNode newPredecessorSubtree = currentPredecessor.createCopyOfSubtree(currentNode);
                currentNewSubtree.successors.add(newPredecessorSubtree);
                newPredecessorSubtree.setPredecessor(currentPredecessor);

                currentNewSubtree = newPredecessorSubtree;
                currentNode = currentPredecessor;
                currentPredecessor = currentPredecessor.getPredecessor();
            }

            rerootedTrees.add(newTree);
        }

        System.out.println("original: " + this);
        System.out.println("rerooted: " + rerootedTrees);
        return rerootedTrees;
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

    public String toJSON() {
        Gson gson = new Gson();
        Map<String, JoinTreeNode> map = new HashMap<>();
        map.put("tree", this);
        return gson.toJson(map);
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
                .stream().map(node -> node.toIndentedString(n + 1)).collect(Collectors.joining("\n"));
        if (!successorsString.isEmpty()) {
            successorsString = "\n" + successorsString;
        }
        return indentation + "JoinTreeNode{" +
                "tables=" + tables +
                ", attributes=" + attributes +
                ", successors=[" + successorsString +
                "], predecessor= " + (predecessor == null ? "null": "not null") +
                "}";
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
