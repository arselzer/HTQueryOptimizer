package query;

import at.ac.tuwien.dbai.hgtools.hypergraph.Edge;
import at.ac.tuwien.dbai.hgtools.sql2hg.*;
import exceptions.QueryConversionException;
import hypergraph.Hyperedge;
import hypergraph.Hypergraph;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SQLQuery {
    private String query;
    private Schema schema;

    public SQLQuery(String query, Schema schema) {
        this.query = query;
        this.schema = schema;
    }

    public SQLQuery(String query, String schemaString) throws QueryConversionException {
        this.query = query;
        this.schema = readSchema(schemaString);
    }

    public static Schema readSchema(String schemaString) throws QueryConversionException {
        Schema result = new Schema();

        Statements schemaStmts = null;
        try {
            schemaStmts = CCJSqlParserUtil.parseStatements(schemaString);
        } catch (JSQLParserException e) {
            throw new QueryConversionException("Error parsing schema: " + e.getMessage());
        }
        for (Statement schemaStmt : schemaStmts.getStatements()) {
            try {
                CreateTable tbl = (CreateTable) schemaStmt;

                // System.out.println("Table: "+tbl.getTable().getName());
                String predicateName = tbl.getTable().getName();
                LinkedList<String> attributes = new LinkedList<>();
                for (ColumnDefinition cdef : tbl.getColumnDefinitions()) {
                    // System.out.println("+++ " + cdef.getColumnName());
                    attributes.add(cdef.getColumnName());
                }
                result.addPredicateDefinition(new PredicateDefinition(predicateName, attributes));
            } catch (ClassCastException c) {
                throw new QueryConversionException("\"" + schemaStmt + "\" is not a CREATE statement.");
            }
        }

        return result;
    }

    public Hypergraph toHypergraph() throws QueryConversionException {
        Statement stmt = null;
        try {
            stmt = CCJSqlParserUtil.parse(query);
        } catch (JSQLParserException e) {
            throw new QueryConversionException("Could not parse query: " + e.getMessage());
        }

        Select selectStmt = (Select) stmt;
        ConjunctiveQueryFinder hgFinder = new ConjunctiveQueryFinder(schema);
        hgFinder.run(selectStmt);

        HypergraphBuilder hgBuilder = new HypergraphBuilder();
        for (Predicate table : hgFinder.getTables()) {
            // System.out.println(table);
            hgBuilder.buildEdge(table);
        }
        for (Predicate table : hgFinder.getTables()) {
            if (table instanceof ViewPredicate) {
                ViewPredicate view = (ViewPredicate) table;
                // System.out.println(view);
                for (Equality join : view.getJoins()) {
                    // System.out.println(join);
                    hgBuilder.buildViewJoin(view.getAlias(), join);
                }
            }
        }
        for (Equality join : hgFinder.getJoins()) {
            // System.out.println(join);
            hgBuilder.buildJoin(join);
        }

        // Convert the hgtools Hypergraph type to the local hypergraph type
        Hypergraph result = new Hypergraph();

        at.ac.tuwien.dbai.hgtools.hypergraph.Hypergraph queryHG = hgBuilder.getHypergraph();

        for (Edge e : queryHG.getEdges()) {
            Hyperedge newEdge = new Hyperedge();
            newEdge.setName(e.getName());
            HashSet<String> edgeSet = new HashSet<>();
            for (String node : e.getVertices()) {
                edgeSet.add(node);
                result.addNode(node);
            }
            newEdge.setNodes(edgeSet);

            result.addEdge(newEdge);
        }

        HashMap<String, String> equivalenceMapping = new HashMap<>();
        for (String key : hgBuilder.getVarToColMapping().keySet()) {
            for (String value : hgBuilder.getVarToColMapping().get(key)) {
                equivalenceMapping.put(value, key);
            }
        }

        result.setEquivalenceMapping(equivalenceMapping);

        return result;
    }
}
