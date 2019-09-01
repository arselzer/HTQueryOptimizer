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
import net.sf.jsqlparser.statement.select.*;
import schema.Column;
import schema.DBSchema;
import schema.Table;

import java.util.*;
import java.util.stream.Collectors;

public class SQLQuery {
    private String query;
    private Schema schema;
    private DBSchema dbSchema;
    private List<String> projectColumns;
    private Statement stmt;

    public SQLQuery(String query, DBSchema dbSchema) throws QueryConversionException {
        this.query = query;
        this.schema = dbSchema.toSchema();
        this.dbSchema = dbSchema;
        determineProjectColumns();
    }

    private void determineProjectColumns() throws QueryConversionException {
        try {
            stmt = CCJSqlParserUtil.parse(query);

            SQLQueryParser queryParser = new SQLQueryParser(stmt, dbSchema);
            projectColumns = queryParser.getProjectColumns();
        } catch (JSQLParserException e) {
            throw new QueryConversionException("Error parsing SQL statement: " + e.getMessage());
        }
    }

    private String buildFinalJoin(Hypergraph hypergraph) throws QueryConversionException {
        String sqlStatement = String.format("SELECT %s\n", String.join(", ", projectColumns));
        sqlStatement += String.format("FROM %s\n",
                hypergraph.getEdges().stream().map(Hyperedge::getName).collect(Collectors.joining(", ")));
        sqlStatement += String.format("WHERE %s", "...");

        return sqlStatement;
    }

    public String toFunction(String functionName) throws QueryConversionException {
        Hypergraph hg = toHypergraph();
        JoinTreeNode joinTree = hg.toJoinTree();

        System.out.println(joinTree);

        String fnStr = "";
        fnStr += String.format("CREATE FUNCTION %s()\n", functionName);

        List<Column> columns = List.of(new Column("a", "int4")); // TODO ...
        List<String> columnDefinitions = columns.stream()
                .map(col -> col.getName() + " " + col.getType()).collect(Collectors.toList());
        fnStr += String.format("RETURNS TABLE (%s) AS $$\n", String.join(",", columnDefinitions));
        fnStr += "BEGIN\n";

        List<Set<JoinTreeNode>> joinLayers = joinTree.getLayers();

        /**
         * Stage 0 - Rename columns to match hypergraph variable
         *
         * Create a view for each table
         */

        for (Hyperedge he: hg.getEdges()) {
            LinkedList<String> columnRewrites = new LinkedList<>();
            String tableName = he.getName();
            for (String variableName : he.getNodes()) {
                String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableName);
                columnRewrites.add(String.format("%s AS %s", columnIdentifier, variableName));
            }
            fnStr += String.format("CREATE TEMP VIEW %s AS\n", "htqo_" + tableName + "_stage_0");
            fnStr += String.format("SELECT %s\n FROM %s;\n", String.join(", ", columnRewrites), tableName);
        }

        /**
         * Stage 1 - join tables inside tree nodes
         *
         * Create a view for each join tree node.
         * Join the tables in the node.
         * This step is redundant with hypertree width 1 and could be optimized away (if it has any significant overhead)
         */

        for (Set<JoinTreeNode> layer: joinLayers) {
            for (JoinTreeNode node : layer) {
                fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(1));
                fnStr += String.format("AS SELECT * FROM %s;\n", String.join(" NATURAL INNER JOIN ", node.getTables()));
            }
        }


        /**
         * Stage 2 - semi joins upwards
         *
         * Create a view for each join tree node from the second one upwards.
         * Join the tables in the node
         */

        for (int i = joinLayers.size()-2 ;i >= 0; i--) {
            Set<JoinTreeNode> layer = joinLayers.get(i);
            fnStr += "-- layer " + i + "\n";

            for (JoinTreeNode node : layer) {
                fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(1));
                fnStr += String.format("AS SELECT * \n");
            }
        }

        // Stage 3 - semi joins downwards

        // Stage 4 - join everything

        fnStr += String.format("RETURN QUERY %s;\n", buildFinalJoin(hg));
        fnStr += "END;\n";
        fnStr += "$$ LANGUAGE plpgsql\n";

        return fnStr;
    }

    public static String generateFunctionName() {
        return "htqo_" + UUID.randomUUID().toString().replace("-", "");
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

                String predicateName = tbl.getTable().getName();
                LinkedList<String> attributes = new LinkedList<>();
                for (ColumnDefinition cdef : tbl.getColumnDefinitions()) {
                    attributes.add(cdef.getColumnName());
                }
                result.addPredicateDefinition(new PredicateDefinition(predicateName, attributes));
            } catch (ClassCastException c) {
                throw new QueryConversionException("\"" + schemaStmt + "\" is not a CREATE statement.");
            }
        }

        return result;
    }

    public List<String> getProjectColumns() {
        return projectColumns;
    }
}
