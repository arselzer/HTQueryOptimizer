package at.ac.tuwien.dbai.hgtools.sql2hg;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

public class ExprVisitorUnsupportedAdapter
        implements ExpressionVisitor, ItemsListVisitor, PivotVisitor, SelectItemVisitor {

    protected static final String NOT_SUPPORTED_YET = "Not supported yet.";

    private static void throwException(Object obj) {
        throw new UnsupportedOperationException("Visiting: " + obj + ". " + NOT_SUPPORTED_YET);
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
        throwException(selectExpressionItem);
    }

    @Override
    public void visit(Pivot pivot) {
        throwException(pivot);
    }

    @Override
    public void visit(PivotXml pivot) {
        throwException(pivot);
    }

    @Override
    public void visit(UnPivot unPivot) {

    }

    @Override
    public void visit(ExpressionList expressionList) {
        throwException(expressionList);
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {
        throwException(namedExpressionList);
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        throwException(multiExprList);
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
    public void visit(IntegerDivision division) {
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
    public void visit(XorExpression xorExpression) {

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
    public void visit(FullTextSearch fullTextSearch) {
        throwException(fullTextSearch);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        throwException(isNullExpression);
    }

    @Override
    public void visit(IsBooleanExpression isBooleanExpression) {
        throwException(isBooleanExpression);
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
    public void visit(SubSelect subSelect) {
        throwException(subSelect);
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
    public void visit(TryCastExpression tryCastExpression) {

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
    public void visit(RowGetExpression rowGetExpression) {

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

    @Override
    public void visit(ArrayExpression aThis) {
        throwException(aThis);
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

}
