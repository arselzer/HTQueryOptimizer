package query.conjunctive;

import hypergraph.Hyperedge;
import hypergraph.Hypergraph;
import schema.Column;
import schema.DBSchema;
import schema.Table;

import java.util.HashMap;
import java.util.List;

public class JoinQuery {
    DBSchema schema;
    List<String> projectColumns;
    List<EquiJoinCondition> joinConditions;

    public JoinQuery(DBSchema schema, List<String> projectColumns, List<EquiJoinCondition> joinConditions) {
        this.schema = schema;
        this.projectColumns = projectColumns;
        this.joinConditions = joinConditions;
    }

    public DBSchema getSchema() {
        return schema;
    }

    public void setSchema(DBSchema schema) {
        this.schema = schema;
    }

    public List<EquiJoinCondition> getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(List<EquiJoinCondition> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public List<String> getProjectColumns() {
        return projectColumns;
    }

    public void setProjectColumns(List<String> projectColumns) {
        this.projectColumns = projectColumns;
    }

    public Hypergraph toHypergraph() {
        Hypergraph hg = new Hypergraph();

        // Maps a column name to an equivalence class
        HashMap<String, String> equivalenceMapping = new HashMap<>();

        int counter = 1;
        // Create all equivalence classes of the join conditions
        for (EquiJoinCondition c : joinConditions) {
            if (!equivalenceMapping.containsKey(c.getCol1()) &&
                    !equivalenceMapping.containsKey(c.getCol2())) {
                String newEC = "v" + counter;
                counter++;
                equivalenceMapping.put(c.getCol1(), newEC);
                equivalenceMapping.put(c.getCol2(), newEC);
            }

            if (equivalenceMapping.containsKey(c.getCol1()) && !equivalenceMapping.containsKey(c.getCol2())) {
                equivalenceMapping.put(c.getCol2(), equivalenceMapping.get(c.getCol1()));
            }

            if (equivalenceMapping.containsKey(c.getCol2()) && !equivalenceMapping.containsKey(c.getCol1())) {
                equivalenceMapping.put(c.getCol1(), equivalenceMapping.get(c.getCol2()));
            }
        }

        // For the remaining variables, create an equivalence class each
        for (Table t : schema.getTables()) {
            for (Column column : t.getColumns()) {
                String identifier = t.getName() + "." + column.getName();
                if (!equivalenceMapping.containsKey(identifier)) {
                    String newEC = "v" + counter;
                    counter++;

                    equivalenceMapping.put(identifier, newEC);
                }
            }
        }

        // For each equivalence class, create a hypergraph nodes
        for (String variable : equivalenceMapping.values()) {
            hg.addNode(variable);
        }

        for (Table t : schema.getTables()) {
            Hyperedge tableHE = new Hyperedge();
            tableHE.setName(t.getName());
            for (Column column : t.getColumns()) {
                String identifier = t.getName() + "." + column.getName();
                if (equivalenceMapping.containsKey(identifier)) {
                    tableHE.getNodes().add(equivalenceMapping.get(identifier));
                }
            }
            hg.addEdge(tableHE);
        }

        hg.setColumnToVariableMapping(equivalenceMapping);

        return hg;
    }

    @Override
    public String toString() {
        return "query.JoinQuery{" +
                "projectColumns=" + projectColumns +
                ", joinConditions=" + joinConditions +
                '}';
    }
}
