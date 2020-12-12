package exceptions;

public class QueryConversionException extends Exception {
    public QueryConversionException(String msg, Exception e) {
        super(msg, e);
    }
    public QueryConversionException(String msg) {
        super(msg);
    }
}
