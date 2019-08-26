import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        Hypergraph<String> hg = new Hypergraph<>();

        // Maps a column name to an equivalence class
        HashMap<String, String> equivalenceMapping = new HashMap<>();

        int counter = 1;
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

        for (String variable : equivalenceMapping.values()) {
            hg.addNode(variable);
        }

        for (Table t : schema.getTables()) {
            Hyperedge<String> tableHE = new Hyperedge<>();
            tableHE.setName(t.getName());
            for (String column : t.getColumns()) {
                tableHE.getNodes().add(equivalenceMapping.get(t.getName() + "." + column));
            }
            hg.addEdge(tableHE);
        }

        return hg;
    }

    @Override
    public String toString() {
        return "JoinQuery{" +
                "projectColumns=" + projectColumns +
                ", joinConditions=" + joinConditions +
                '}';
    }
}
