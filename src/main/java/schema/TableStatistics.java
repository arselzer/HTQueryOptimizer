package schema;

import java.util.Map;

public class TableStatistics {
    private int rowCount;
    private Map<String, Map<String, Double>> mostCommonFrequencies;

    public TableStatistics(int rowCount, Map<String, Map<String, Double>> mostCommonFrequencies) {
        this.rowCount = rowCount;
        this.mostCommonFrequencies = mostCommonFrequencies;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public Map<String, Map<String, Double>> getMostCommonFrequencies() {
        return mostCommonFrequencies;
    }

    public void setMostCommonFrequencies(Map<String, Map<String, Double>> mostCommonFrequencies) {
        this.mostCommonFrequencies = mostCommonFrequencies;
    }
}
