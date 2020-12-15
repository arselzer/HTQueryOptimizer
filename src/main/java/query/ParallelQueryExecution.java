package query;

import schema.Column;

import java.util.List;

public class ParallelQueryExecution {
    private List<List<String>> sqlStatements;
    private DropStatements dropStatements;
    private List<Column> resultColumns;
    private String finalSelectName;

    public ParallelQueryExecution(List<List<String>> sqlStatements, DropStatements dropStatements, List<Column> resultColumns, String finalSelectName) {
        this.sqlStatements = sqlStatements;
        this.dropStatements = dropStatements;
        this.resultColumns = resultColumns;
        this.finalSelectName = finalSelectName;
    }

    public List<List<String>> getSqlStatements() {
        return sqlStatements;
    }

    public void setSqlStatements(List<List<String>> sqlStatements) {
        this.sqlStatements = sqlStatements;
    }

    public DropStatements getDropStatements() {
        return dropStatements;
    }

    public void setDropStatements(DropStatements dropStatements) {
        this.dropStatements = dropStatements;
    }

    public List<Column> getResultColumns() {
        return resultColumns;
    }

    public void setResultColumns(List<Column> resultColumns) {
        this.resultColumns = resultColumns;
    }

    public String getFinalSelectName() {
        return finalSelectName;
    }

    public void setFinalSelectName(String finalSelectName) {
        this.finalSelectName = finalSelectName;
    }
}
