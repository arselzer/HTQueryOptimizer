package query;

import at.ac.tuwien.dbai.hgtools.sql2hg.PredicateDefinition;
import at.ac.tuwien.dbai.hgtools.sql2hg.Schema;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.LinkedList;
import java.util.List;

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

            otherSchema.addPredicateDefinition(new PredicateDefinition(predicateName, table.getColumns()));
        }

        return otherSchema;
    }

    @Override
    public String toString() {
        return "query.DBSchema{" +
                "tables=" + tables +
                '}';
    }
}
