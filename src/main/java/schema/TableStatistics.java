package schema;

import java.util.Map;

public class TableStatistics {
    private Map<Column, Integer> rowCount;
    private Map<Column, String[]> mostCommonValues;
    private Map<Column, Double[]> mostCommonFrequencies;

    public TableStatistics(Map<Column, Integer> rowCount, Map<Column, String[]> mostCommonValues, Map<Column, Double[]> mostCommonFrequencies) {
        this.rowCount = rowCount;
        this.mostCommonValues = mostCommonValues;
        this.mostCommonFrequencies = mostCommonFrequencies;
    }

    public Map<Column, Integer> getRowCount() {
        return rowCount;
    }

    public void setRowCount(Map<Column, Integer> rowCount) {
        this.rowCount = rowCount;
    }

    public Map<Column, String[]> getMostCommonValues() {
        return mostCommonValues;
    }

    public void setMostCommonValues(Map<Column, String[]> mostCommonValues) {
        this.mostCommonValues = mostCommonValues;
    }

    public Map<Column, Double[]> getMostCommonFrequencies() {
        return mostCommonFrequencies;
    }

    public void setMostCommonFrequencies(Map<Column, Double[]> mostCommonFrequencies) {
        this.mostCommonFrequencies = mostCommonFrequencies;
    }
}
