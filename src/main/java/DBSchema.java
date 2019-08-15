import java.util.LinkedList;
import java.util.List;

public class DBSchema {
    List<Table> tables;

    public DBSchema() {
        tables = new LinkedList<>();
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "DBSchema{" +
                "tables=" + tables +
                '}';
    }
}
