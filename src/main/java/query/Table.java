package query;

import java.util.List;

public class Table {
    private String name;
    private List<String> columns;

    public int getNumberOfColumns() {
        return columns.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "query.Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }
}
