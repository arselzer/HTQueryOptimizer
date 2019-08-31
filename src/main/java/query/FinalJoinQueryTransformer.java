package query;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.LinkedList;

public class FinalJoinQueryTransformer implements SelectVisitor, FromItemVisitor {
    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.setJoins(new LinkedList<>());
        plainSelect.getFromItem().accept(this);
    }

    @Override
    public void visit(SetOperationList setOperationList) {

    }

    @Override
    public void visit(WithItem withItem) {

    }

    @Override
    public void visit(ValuesStatement valuesStatement) {

    }

    @Override
    public void visit(Table table) {

    }

    @Override
    public void visit(SubSelect subSelect) {

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
}
