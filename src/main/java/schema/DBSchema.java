package schema;

import at.ac.tuwien.dbai.hgtools.sql2hg.PredicateDefinition;
import at.ac.tuwien.dbai.hgtools.sql2hg.Schema;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DBSchema {
    private List<Table> tables;

    public DBSchema() {
        tables = new LinkedList<>();
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public Schema toSchema() {
        Schema otherSchema = new Schema();

        for (Table table : tables) {
            String predicateName = table.getName();

            // Convert DBSchema to Schema (the latter does not include type information on columns)
            otherSchema.addPredicateDefinition(new PredicateDefinition(predicateName,
                    table.getColumns().stream().map(Column::getName).collect(Collectors.toList())));
        }

        return otherSchema;
    }

    public DBSchema fromString(String schemaStr) {
        // TODO implement
        return null;
    }

    @Override
    public String toString() {
        return "DBSchema{" +
                "tables=" + tables +
                '}';
    }
}
