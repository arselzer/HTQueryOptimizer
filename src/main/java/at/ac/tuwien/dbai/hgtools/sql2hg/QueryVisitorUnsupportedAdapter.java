package at.ac.tuwien.dbai.hgtools.sql2hg;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class QueryVisitorUnsupportedAdapter
        implements StatementVisitor, SelectVisitor, SelectItemVisitor, FromItemVisitor, ExpressionVisitor {

    protected static final String NOT_SUPPORTED_YET = "Not supported yet.";

    private static void throwException(Object obj) {
        throw new UnsupportedOperationException("Visiting: " + obj + ". " + NOT_SUPPORTED_YET);
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        throwException(plainSelect);
    }

    @Override
    public void visit(SetOperationList setOpList) {
        throwException(setOpList);
    }

    @Override
    public void visit(WithItem withItem) {
        throwException(withItem);
    }

    @Override
    public void visit(Comment comment) {
        throwException(comment);
    }

    @Override
    public void visit(Commit commit) {
        throwException(commit);
    }

    @Override
    public void visit(Delete delete) {
        throwException(delete);
    }

    @Override
    public void visit(Update update) {
        throwException(update);
    }

    @Override
    public void visit(Insert insert) {
        throwException(insert);
    }

    @Override
    public void visit(Replace replace) {
        throwException(replace);
    }

    @Override
    public void visit(Drop drop) {
        throwException(drop);
    }

    @Override
    public void visit(Truncate truncate) {
        throwException(truncate);
    }

    @Override
    public void visit(CreateIndex createIndex) {
        throwException(createIndex);
    }

    @Override
    public void visit(CreateTable createTable) {
        throwException(createTable);
    }

    @Override
    public void visit(CreateView createView) {
        throwException(createView);
    }

    @Override
    public void visit(AlterView alterView) {
        throwException(alterView);
    }

    @Override
    public void visit(Alter alter) {
        throwException(alter);
    }

    @Override
    public void visit(Statements stmts) {
        throwException(stmts);
    }

    @Override
    public void visit(Execute execute) {
        throwException(execute);
    }

    @Override
    public void visit(SetStatement set) {
        throwException(set);
    }

    @Override
    public void visit(ShowColumnsStatement set) {
        throwException(set);
    }

    @Override
    public void visit(Merge merge) {
        throwException(merge);
    }

    @Override
    public void visit(Select select) {
        throwException(select);
    }

    @Override
    public void visit(Upsert upsert) {
        throwException(upsert);
    }

    @Override
    public void visit(UseStatement use) {
        throwException(use);
    }

    @Override
    public void visit(Block block) {
        throwException(block);
    }

    @Override
    public void visit(ValuesStatement values) {
        throwException(values);
    }

    @Override
    public void visit(DescribeStatement describe) {
        throwException(describe);
    }

    @Override
    public void visit(ExplainStatement aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(ShowStatement aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(AllColumns allColumns) {
        throwException(allColumns);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        throwException(allTableColumns);
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        throwException(selectExpressionItem);
    }

    @Override
    public void visit(Table tableName) {
        throwException(tableName);
    }

    @Override
    public void visit(SubSelect subSelect) {
        throwException(subSelect);
    }

    @Override
    public void visit(SubJoin subjoin) {
        throwException(subjoin);
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        throwException(lateralSubSelect);
    }

    @Override
    public void visit(ValuesList valuesList) {
        throwException(valuesList);
    }

    @Override
    public void visit(TableFunction tableFunction) {
        throwException(tableFunction);
    }

    @Override
    public void visit(ParenthesisFromItem aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(BitwiseRightShift aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(BitwiseLeftShift aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(NullValue nullValue) {
        throwException(nullValue);
    }

    @Override
    public void visit(Function function) {
        throwException(function);
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        throwException(signedExpression);
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        throwException(jdbcParameter);
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        throwException(jdbcNamedParameter);
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        throwException(doubleValue);
    }

    @Override
    public void visit(LongValue longValue) {
        throwException(longValue);
    }

    @Override
    public void visit(HexValue hexValue) {
        throwException(hexValue);
    }

    @Override
    public void visit(DateValue dateValue) {
        throwException(dateValue);
    }

    @Override
    public void visit(TimeValue timeValue) {
        throwException(timeValue);
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        throwException(timestampValue);
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        throwException(parenthesis);
    }

    @Override
    public void visit(StringValue stringValue) {
        throwException(stringValue);
    }

    @Override
    public void visit(Addition addition) {
        throwException(addition);
    }

    @Override
    public void visit(Division division) {
        throwException(division);
    }

    @Override
    public void visit(Multiplication multiplication) {
        throwException(multiplication);
    }

    @Override
    public void visit(Subtraction subtraction) {
        throwException(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {
        throwException(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        throwException(orExpression);
    }

    @Override
    public void visit(Between between) {
        throwException(between);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        throwException(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        throwException(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        throwException(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        throwException(inExpression);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        throwException(isNullExpression);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        throwException(likeExpression);
    }

    @Override
    public void visit(MinorThan minorThan) {
        throwException(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        throwException(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        throwException(notEqualsTo);
    }

    @Override
    public void visit(Column tableColumn) {
        throwException(tableColumn);
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        throwException(caseExpression);
    }

    @Override
    public void visit(WhenClause whenClause) {
        throwException(whenClause);
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        throwException(existsExpression);
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        throwException(allComparisonExpression);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        throwException(anyComparisonExpression);
    }

    @Override
    public void visit(Concat concat) {
        throwException(concat);
    }

    @Override
    public void visit(Matches matches) {
        throwException(matches);
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        throwException(bitwiseAnd);
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        throwException(bitwiseOr);
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        throwException(bitwiseXor);
    }

    @Override
    public void visit(CastExpression cast) {
        throwException(cast);
    }

    @Override
    public void visit(Modulo modulo) {
        throwException(modulo);
    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        throwException(aexpr);
    }

    @Override
    public void visit(ExtractExpression eexpr) {
        throwException(eexpr);
    }

    @Override
    public void visit(IntervalExpression iexpr) {
        throwException(iexpr);
    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        throwException(oexpr);
    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        throwException(rexpr);
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        throwException(jsonExpr);
    }

    @Override
    public void visit(JsonOperator jsonExpr) {
        throwException(jsonExpr);
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        throwException(regExpMySQLOperator);
    }

    @Override
    public void visit(UserVariable var) {
        throwException(var);
    }

    @Override
    public void visit(NumericBind bind) {
        throwException(bind);
    }

    @Override
    public void visit(KeepExpression aexpr) {
        throwException(aexpr);
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        throwException(groupConcat);
    }

    @Override
    public void visit(ValueListExpression valueList) {
        throwException(valueList);
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        throwException(rowConstructor);
    }

    @Override
    public void visit(OracleHint hint) {
        throwException(hint);
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        throwException(timeKeyExpression);
    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
        throwException(literal);
    }

    @Override
    public void visit(NotExpression aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(NextValExpression aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(CollateExpression aThis) {
        throwException(aThis);
    }

    @Override
    public void visit(SimilarToExpression aThis) {
        throwException(aThis);
    }

}
