public class Validators {
    public static boolean isValidSQLIdentifier(String str) {
        // https://www.postgresql.org/docs/9.4/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS
        // TODO allow non-latin letters
        return str.matches("^[a-zA-Z_][a-zA-Z0-9_\\$]*$");
    }
}
