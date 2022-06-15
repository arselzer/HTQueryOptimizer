package at.ac.tuwien.dbai.hgtools.sql2hg;

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
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.ParenthesisFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class QueryVisitorNoExpressionAdapter
		implements StatementVisitor, SelectVisitor, SelectItemVisitor, FromItemVisitor {

	@Override
	public void visit(Table tableName) {
		// empty implementation
	}

	@Override
	public void visit(SubSelect subSelect) {
		// empty implementation
	}

	@Override
	public void visit(SubJoin subjoin) {
		// empty implementation
	}

	@Override
	public void visit(LateralSubSelect lateralSubSelect) {
		// empty implementation
	}

	@Override
	public void visit(ValuesList valuesList) {
		// empty implementation
	}

	@Override
	public void visit(TableFunction tableFunction) {
		// empty implementation
	}

	@Override
	public void visit(ParenthesisFromItem aThis) {
		// empty implementation
	}

	@Override
	public void visit(AllColumns allColumns) {
		// empty implementation
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {
		// empty implementation
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		// empty implementation
	}

	@Override
	public void visit(SavepointStatement savepointStatement) {

	}

	@Override
	public void visit(RollbackStatement rollbackStatement) {

	}

	@Override
	public void visit(Comment comment) {
		// empty implementation
	}

	@Override
	public void visit(Commit commit) {
		// empty implementation
	}

	@Override
	public void visit(Delete delete) {
		// empty implementation
	}

	@Override
	public void visit(Update update) {
		// empty implementation
	}

	@Override
	public void visit(Insert insert) {
		// empty implementation
	}

	@Override
	public void visit(Replace replace) {
		// empty implementation
	}

	@Override
	public void visit(Drop drop) {
		// empty implementation
	}

	@Override
	public void visit(Truncate truncate) {
		// empty implementation
	}

	@Override
	public void visit(CreateIndex createIndex) {
		// empty implementation
	}

	@Override
	public void visit(CreateSchema createSchema) {

	}

	@Override
	public void visit(CreateTable createTable) {
		// empty implementation
	}

	@Override
	public void visit(CreateView createView) {
		// empty implementation
	}

	@Override
	public void visit(AlterView alterView) {
		// empty implementation
	}

	@Override
	public void visit(Alter alter) {
		// empty implementation
	}

	@Override
	public void visit(Statements stmts) {
		// empty implementation
	}

	@Override
	public void visit(Execute execute) {
		// empty implementation
	}

	@Override
	public void visit(SetStatement set) {
		// empty implementation
	}

	@Override
	public void visit(ResetStatement resetStatement) {

	}

	@Override
	public void visit(ShowColumnsStatement set) {
		// empty implementation
	}

	@Override
	public void visit(ShowTablesStatement showTablesStatement) {

	}

	@Override
	public void visit(Merge merge) {
		// empty implementation
	}

	@Override
	public void visit(Select select) {
		// empty implementation
	}

	@Override
	public void visit(Upsert upsert) {
		// empty implementation
	}

	@Override
	public void visit(UseStatement use) {
		// empty implementation
	}

	@Override
	public void visit(Block block) {
		// empty implementation
	}

	@Override
	public void visit(ValuesStatement values) {
		// empty implementation
	}

	@Override
	public void visit(DescribeStatement describe) {
		// empty implementation
	}

	@Override
	public void visit(ExplainStatement aThis) {
		// empty implementation
	}

	@Override
	public void visit(ShowStatement aThis) {
		// empty implementation
	}

	@Override
	public void visit(DeclareStatement aThis) {
		// empty implementation
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

	@Override
	public void visit(PlainSelect plainSelect) {
		// empty implementation
	}

	@Override
	public void visit(SetOperationList setOpList) {
		// empty implementation
	}

	@Override
	public void visit(WithItem withItem) {
		// empty implementation
	}

}
