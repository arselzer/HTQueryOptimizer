package query;

import schema.Column;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionBuilder {
    private String functionName;
    private List<Column> columns;

    public FunctionBuilder(String functionName) {
        this.functionName = functionName;
    }

    public FunctionBuilder projectColumns(List<Column> columns) {
        this.columns = columns;

        return this;
    }

    public String build() {
        String fnStr = "";
        fnStr += String.format("CREATE FUNCTION %s()\n", functionName);

        List<String> columnDefinitions = columns.stream()
                .map(col -> col.getName() + " " + col.getType()).collect(Collectors.toList());
        fnStr += String.format("RETURNS TABLE (%s) AS $$\n", String.join(",", columnDefinitions));
        fnStr += "BEGIN\n";

        fnStr += "RETURN QUERY SELECT 1;\n";
        fnStr += "END;\n";
        fnStr += "$$ LANGUAGE plpgsql\n";

        return fnStr;
    }
}
