package query;

public class FunctionBuilder {
    private String functionName;

    public FunctionBuilder(String functionName) {
        this.functionName = functionName;
    }

    public String build() {
        String fnStr = "";
        fnStr += String.format("CREATE FUNCTION %s()\n", functionName);

        return fnStr;
    }
}
