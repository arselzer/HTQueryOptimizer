package query;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to keep track of tables and views to drop (in the correct order)
 *
 * IF EXISTS is used for the error case where query execution fails and error handling removes the partially created tables
 */
public class DropStatements {
    private LinkedList<String> dropStrings = new LinkedList<>();

    public void dropTable(String name) {
        dropStrings.addFirst(String.format("DROP TABLE IF EXISTS %s;", name));
    }

    public void dropView(String name) {
        dropStrings.addFirst(String.format("DROP VIEW IF EXISTS %s;", name));
    }

    public String toString() {
        return String.join("\n", dropStrings);
    }
    public List<String> toList() {
        return dropStrings;
    }
}
