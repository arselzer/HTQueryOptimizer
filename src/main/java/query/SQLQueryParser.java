package query;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterSession;
import net.sf.jsqlparser.statement.alter.AlterSystemStatement;
import net.sf.jsqlparser.statement.alter.RenameTableStatement;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.sequence.CreateSequence;
import net.sf.jsqlparser.statement.create.synonym.CreateSynonym;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import schema.DBSchema;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLQueryParser implements StatementVisitor, SelectVisitor, SelectItemVisitor, ExpressionVisitor, FromItemVisitor {
    private DBSchema schema;
    private ParseState state;
    private List<String> projectColumns = new LinkedList<>();
    private Map<String, String> aliases = new HashMap<>();
    private Set<String> tables = new HashSet<>();

    public SQLQueryParser(Statement stmt, DBSchema schema) {
        this.schema = schema;
        state = ParseState.SEARCHING;
        stmt.accept(this);
    }

    private void unsupported(String name) {
        throw new UnsupportedOperationException("Operation is not supported: " + name);
    }

    public List<String> getProjectColumns() {
        return projectColumns;
    }

    public Set<String> getTables() {
        return tables;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    @Override
    public void visit(Select select) {
        select.getSelectBody().accept(this);
        // -> PlainSelect, SetOperations
    }

    @Override
    public void visit(Block block) {
        block.accept(this);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getSelectItems().forEach(selectItem -> selectItem.accept(this));
        plainSelect.getSelectItems().forEach(selectItem -> {
            System.out.println(selectItem.toString());
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher m = p.matcher(selectItem.toString());
            if (m.find()) {
                System.out.println(m.group(1));
                projectColumns.add(m.group(1));
            }
        });
        plainSelect.getFromItem().accept(this);
        plainSelect.getJoins().forEach(join -> join.getRightItem().accept(this));
    }

    @Override
    public void visit(AllColumns allColumns) {
        projectColumns = List.of("*");
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {

    }

    @Override
    public void visit(AllValue allValue) {

    }

    @Override
    public void visit(IsDistinctExpression isDistinctExpression) {

    }

    @Override
    public void visit(GeometryDistance geometryDistance) {

    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this);
    }

    @Override
    public void visit(net.sf.jsqlparser.schema.Column column) {
        if (column.getTable() != null) {
            projectColumns.add(column.getFullyQualifiedName());

        } else {
            // The column name might still be ambiguous
            projectColumns.add(column.getColumnName());
        }
    }

    @Override
    public void visit(SetOperationList setOperationList) {
        unsupported("Set operations");
        // TODO implement set operations
    }

    @Override
    public void visit(Statements statements) {
        statements.getStatements().forEach(stmt -> stmt.accept(this));
    }

    @Override
    public void visit(ValuesStatement valuesStatement) {
        // ?
        System.out.println("valuesStatement: " + valuesStatement);
    }

    @Override
    public void visit(SavepointStatement savepointStatement) {

    }

    @Override
    public void visit(RollbackStatement rollbackStatement) {

    }

    @Override
    public void visit(Comment comment) {

    }

    @Override
    public void visit(UseStatement useStatement) {
        // ?
    }

    @Override
    public void visit(WithItem withItem) {
        // TODO support with
    }

    @Override
    public void visit(ShowColumnsStatement showColumnsStatement) {
        // ?
    }

    @Override
    public void visit(ShowTablesStatement showTablesStatement) {

    }

    @Override
    public void visit(Table table) {
        Alias alias = table.getAlias();
        if (alias != null) {
            tables.add(alias.getName());
            aliases.put(alias.getName(), table.getName());
        } else {
            tables.add(table.getName());
            aliases.put(table.getName(), table.getName());
        }
    }

    @Override
    public void visit(SubSelect subSelect) {
        // TODO support subselect
    }

    @Override
    public void visit(SubJoin subJoin) {

    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {

    }

    @Override
    public void visit(ValuesList valuesList) {

    }

    @Override
    public void visit(TableFunction tableFunction) {

    }

    @Override
    public void visit(ParenthesisFromItem parenthesisFromItem) {

    }

    @Override
    public void visit(BitwiseRightShift bitwiseRightShift) {

    }

    @Override
    public void visit(BitwiseLeftShift bitwiseLeftShift) {

    }

    @Override
    public void visit(NullValue nullValue) {

    }

    @Override
    public void visit(Function function) {

    }

    @Override
    public void visit(SignedExpression signedExpression) {

    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {

    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {

    }

    @Override
    public void visit(DoubleValue doubleValue) {

    }

    @Override
    public void visit(LongValue longValue) {

    }

    @Override
    public void visit(HexValue hexValue) {

    }

    @Override
    public void visit(DateValue dateValue) {

    }

    @Override
    public void visit(TimeValue timeValue) {

    }

    @Override
    public void visit(TimestampValue timestampValue) {

    }

    @Override
    public void visit(Parenthesis parenthesis) {

    }

    @Override
    public void visit(StringValue stringValue) {

    }

    @Override
    public void visit(Addition addition) {

    }

    @Override
    public void visit(Division division) {

    }

    @Override
    public void visit(IntegerDivision integerDivision) {

    }

    @Override
    public void visit(Multiplication multiplication) {

    }

    @Override
    public void visit(Subtraction subtraction) {

    }

    @Override
    public void visit(AndExpression andExpression) {

    }

    @Override
    public void visit(OrExpression orExpression) {

    }

    @Override
    public void visit(XorExpression xorExpression) {

    }

    @Override
    public void visit(Between between) {

    }

    @Override
    public void visit(EqualsTo equalsTo) {

    }

    @Override
    public void visit(GreaterThan greaterThan) {

    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {

    }

    @Override
    public void visit(InExpression inExpression) {

    }

    @Override
    public void visit(FullTextSearch fullTextSearch) {

    }

    @Override
    public void visit(IsNullExpression isNullExpression) {

    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {

    }

    @Override
    public void visit(LikeExpression likeExpression) {

    }

    @Override
    public void visit(MinorThan minorThan) {

    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {

    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {

    }

    @Override
    public void visit(CaseExpression caseExpression) {

    }

    @Override
    public void visit(WhenClause whenClause) {

    }

    @Override
    public void visit(ExistsExpression existsExpression) {

    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {

    }

    @Override
    public void visit(Concat concat) {

    }

    @Override
    public void visit(Matches matches) {

    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {

    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {

    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {

    }

    @Override
    public void visit(CastExpression castExpression) {

    }

    @Override
    public void visit(TryCastExpression tryCastExpression) {

    }

    @Override
    public void visit(Modulo modulo) {

    }

    @Override
    public void visit(AnalyticExpression analyticExpression) {
        System.out.println("analytic:" + analyticExpression);
    }

    @Override
    public void visit(ExtractExpression extractExpression) {

    }

    @Override
    public void visit(IntervalExpression intervalExpression) {

    }

    @Override
    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

    }

    @Override
    public void visit(RegExpMatchOperator regExpMatchOperator) {

    }

    @Override
    public void visit(JsonExpression jsonExpression) {

    }

    @Override
    public void visit(JsonOperator jsonOperator) {

    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {

    }

    @Override
    public void visit(UserVariable userVariable) {

    }

    @Override
    public void visit(NumericBind numericBind) {

    }

    @Override
    public void visit(KeepExpression keepExpression) {

    }

    @Override
    public void visit(MySQLGroupConcat mySQLGroupConcat) {

    }

    @Override
    public void visit(ValueListExpression valueListExpression) {

    }

    @Override
    public void visit(RowConstructor rowConstructor) {

    }

    @Override
    public void visit(RowGetExpression rowGetExpression) {

    }

    @Override
    public void visit(OracleHint oracleHint) {

    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {

    }

    @Override
    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {

    }

    @Override
    public void visit(NotExpression notExpression) {

    }

    @Override
    public void visit(NextValExpression nextValExpression) {

    }

    @Override
    public void visit(CollateExpression collateExpression) {

    }

    @Override
    public void visit(SimilarToExpression similarToExpression) {

    }

    @Override
    public void visit(ArrayExpression arrayExpression) {

    }

    @Override
    public void visit(ArrayConstructor arrayConstructor) {

    }

    @Override
    public void visit(VariableAssignment variableAssignment) {

    }

    @Override
    public void visit(XMLSerializeExpr xmlSerializeExpr) {

    }

    @Override
    public void visit(TimezoneExpression timezoneExpression) {

    }

    @Override
    public void visit(JsonAggregateFunction jsonAggregateFunction) {

    }

    @Override
    public void visit(JsonFunction jsonFunction) {

    }

    @Override
    public void visit(ConnectByRootOperator connectByRootOperator) {

    }

    @Override
    public void visit(OracleNamedFunctionParameter oracleNamedFunctionParameter) {

    }

    @Override
    public void visit(Commit commit) {
        unsupported("Transactions");
    }

    @Override
    public void visit(Delete delete) {
        unsupported("Delete");
    }

    @Override
    public void visit(Update update) {
        unsupported("Update");
    }

    @Override
    public void visit(Insert insert) {
        unsupported("Insert");
    }

    @Override
    public void visit(Replace replace) {
        unsupported("Replace");
    }

    @Override
    public void visit(Drop drop) {
        unsupported("Drop");
    }

    @Override
    public void visit(Truncate truncate) {
        unsupported("Truncate");
    }

    @Override
    public void visit(CreateIndex createIndex) {
        unsupported("CREATE INDEX");
    }

    @Override
    public void visit(CreateSchema createSchema) {

    }

    @Override
    public void visit(CreateTable createTable) {
        unsupported("CREATE TABLE");
    }

    @Override
    public void visit(CreateView createView) {
        unsupported("CREATE VIEW");
    }

    @Override
    public void visit(AlterView alterView) {
        unsupported("ALTER VIEW");
    }

    @Override
    public void visit(Alter alter) {
        unsupported("ALTER");
    }

    @Override
    public void visit(Execute execute) {
        unsupported("Execute");
    }

    @Override
    public void visit(SetStatement setStatement) {
        unsupported("SET");
    }

    @Override
    public void visit(ResetStatement resetStatement) {

    }

    @Override
    public void visit(Merge merge) {
        unsupported("MERGE");
    }

    @Override
    public void visit(Upsert upsert) {
        unsupported("Upsert");
    }

    @Override
    public void visit(DescribeStatement describeStatement) {
        unsupported("DESCRIBE");
    }

    @Override
    public void visit(ExplainStatement explainStatement) {
        unsupported("EXPLAIN");
    }

    @Override
    public void visit(ShowStatement showStatement) {
        unsupported("SHOW");
    }

    @Override
    public void visit(DeclareStatement declareStatement) {

    }

    @Override
    public void visit(Grant grant) {

    }

    @Override
    public void visit(CreateSequence createSequence) {

    }

    @Override
    public void visit(AlterSequence alterSequence) {

    }

    @Override
    public void visit(CreateFunctionalStatement createFunctionalStatement) {

    }

    @Override
    public void visit(CreateSynonym createSynonym) {

    }

    @Override
    public void visit(AlterSession alterSession) {

    }

    @Override
    public void visit(IfElseStatement ifElseStatement) {

    }

    @Override
    public void visit(RenameTableStatement renameTableStatement) {

    }

    @Override
    public void visit(PurgeStatement purgeStatement) {

    }

    @Override
    public void visit(AlterSystemStatement alterSystemStatement) {

    }

    private enum ParseState {
        SEARCHING,
        COLUMN_LIST
    }
}
