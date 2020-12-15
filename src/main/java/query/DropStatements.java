package query;

import java.util.LinkedList;

/**
 * Class to keep track of tables and views to drop (in the correct order)
 */
public class DropStatements {
    private LinkedList<String> dropStrings = new LinkedList<>();

    public void dropTable(String name) {
        dropStrings.addFirst(String.format("DROP TABLE %s;", name));
    }

    public void dropView(String name) {
        dropStrings.addFirst(String.format("DROP VIEW %s;", name));
    }

    public String toString() {
        return String.join("\n", dropStrings);
    }
}
