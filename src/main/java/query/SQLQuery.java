package query;

import at.ac.tuwien.dbai.hgtools.hypergraph.Edge;
import at.ac.tuwien.dbai.hgtools.sql2hg.*;
import exceptions.JoinTreeGenerationException;
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
import schema.Column;
import schema.DBSchema;
import schema.Table;

import java.util.*;
import java.util.stream.Collectors;

public class SQLQuery {
    private String query;
    private Schema schema;
    private DBSchema dbSchema;
    private Map<String, Column> columnByNameMap;
    private List<String> projectColumns;
    private Statement stmt;

    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;

    private boolean explicitStage0 = false;

    public SQLQuery(String query, DBSchema dbSchema) throws QueryConversionException {
        this.query = query;
        this.schema = dbSchema.toSchema();
        this.dbSchema = dbSchema;
        determineProjectColumns();
        buildColumnLookup();
    }

    public static String generateFunctionName() {
        return "htqo_" + UUID.randomUUID().toString().replace("-", "");
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

    private void determineProjectColumns() throws QueryConversionException {
        try {
            stmt = CCJSqlParserUtil.parse(query);

            SQLQueryParser queryParser = new SQLQueryParser(stmt, dbSchema);
            projectColumns = queryParser.getProjectColumns();
        } catch (JSQLParserException e) {
            throw new QueryConversionException("Error parsing SQL statement: " + e.getMessage());
        }
    }

    private void buildColumnLookup() {
        columnByNameMap = new HashMap<>();
        for (Table t : dbSchema.getTables()) {
            for (Column c : t.getColumns()) {
                columnByNameMap.put(t.getName() + "." + c.getName(), c);
            }
        }
    }

    public String toFunction(String functionName) throws QueryConversionException {
        Hypergraph hg = toHypergraph();
        this.hypergraph = hg;
        try {
            joinTree = hg.toJoinTree();
        } catch (JoinTreeGenerationException e) {
            throw new QueryConversionException("Error generating join tree: " + e.getMessage());
        }

        List<String> tempTables = new LinkedList<>();
        List<String> tempViews = new LinkedList<>();

        //System.out.println(joinTree);

        String fnStr = "";
        fnStr += String.format("CREATE FUNCTION %s()\n", functionName);

        List<Column> resultColumns = new LinkedList<>();

        if (projectColumns.size() == 1 && projectColumns.get(0).equals("*")) {
            // Go through all hypergraph vertices
            for (String node : hg.getNodes()) {
                // Look up the column name for the vertex name - get any actual column
                // from any table associated - the type has to be equal anyway
                Map<String, List<String>> nodeCols = hg.getInverseEquivalenceMapping().get(node);
                // Get any table name
                String table = (String) nodeCols.keySet().toArray()[0];
                // Build the table.column identifier
                String identifier = table + "." + nodeCols.get(table).get(0);

                Column realColumn = columnByNameMap.get(identifier);

                resultColumns.add(new Column(node, realColumn.getType()));
            }
        } else {
            for (String projectCol : projectColumns) {
                Column realColumn = columnByNameMap.get(projectCol);
                String hyperedge = hg.getColumnToVariableMapping().get(projectCol);
                Column newColumn = new Column(hyperedge, realColumn.getType());
                // Check if the same column isn't already part of the output.
                // The same variable might have been specified in multiple columns
                if (!resultColumns.contains(newColumn)) {
                    resultColumns.add(newColumn);
                }
                // TODO support non-fully qualified columns
            }
        }

        // Set up a lookup table of tables for quickly getting the variables of a hyperedge
        Map<String, Table> tablesByName = new HashMap<>();
        for (Table t : dbSchema.getTables()) {
            tablesByName.put(t.getName(), t);
        }

        List<String> columnDefinitions = resultColumns.stream()
                .map(col -> col.getName() + " " + col.getType()).collect(Collectors.toList());
        fnStr += String.format("RETURNS TABLE (%s) AS $$\n", String.join(",", columnDefinitions));

        // There is a variable conflict between the output variable and column name
        fnStr += "#variable_conflict use_column\n";
        fnStr += "BEGIN\n";

        List<Set<JoinTreeNode>> joinLayers = joinTree.getLayers();

        /**
         * Stage 0 - Rename columns to match hypergraph variable
         *
         * Create a view for each table
         */
        //fnStr += "-- STAGE 0\n";

        if (explicitStage0) {
            for (Hyperedge he : hg.getEdges()) {
                LinkedList<String> columnRewrites = new LinkedList<>();
                String tableName = he.getName();
                for (String variableName : he.getNodes()) {
                    String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableName).get(0);
                    columnRewrites.add(String.format("%s AS %s", columnIdentifier, variableName));
                }
                String tempViewName = "htqo_" + tableName + "_stage_0";
                fnStr += String.format("CREATE TEMP VIEW %s AS\n", tempViewName);
                tempViews.add(tempViewName);
                fnStr += String.format("SELECT %s\n FROM %s;\n", String.join(", ", columnRewrites), tableName);
            }
        }

        /**
         * Stage 1 - join tables inside tree nodes
         *
         * Create a view for each join tree node.
         * Join the tables in the node.
         * This step is redundant with hypertree width 1 and could be optimized away (if it has any significant overhead)
         */
        //fnStr += "-- STAGE 1\n";

        if (explicitStage0) {
            for (Set<JoinTreeNode> layer : joinLayers) {
                for (JoinTreeNode node : layer) {
                    fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(1));
                    tempTables.add(node.getIdentifier(1));
                    fnStr += String.format("AS SELECT DISTINCT * FROM %s;\n", node.getTables()
                            .stream().map(tblName -> "htqo_" + tblName + "_stage_0")
                            .collect(Collectors.joining(" NATURAL INNER JOIN ")));
                }
            }
        } else {
            for (Set<JoinTreeNode> layer : joinLayers) {
                for (JoinTreeNode node : layer) {
                    LinkedList<String> aliasedTables = new LinkedList<>();

                    for (String tableName : node.getTables()) {
                        LinkedList<String> columnRewrites = new LinkedList<>();
                        Hyperedge he = hg.getEdgeByName(tableName);
                        for (String variableName : he.getNodes()) {
                            // Get only the first as any of the equivalent is sufficient
                            String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableName).get(0);
                            columnRewrites.add(String.format("%s AS %s", columnIdentifier, variableName));
                        }

                        // Check if there are more variables than columns -> Some columns are equivalent and
                        // a filter checking this needs to be added
                        //System.out.println(he.getNodes().size() + " " + tablesByName.get(tableName).getColumns().size());
                        if (he.getNodes().size() < tablesByName.get(tableName).getColumns().size()) {
                            List<String> whereConditions = new LinkedList<>();
                            for (String variable : he.getNodes()) {
                                //Map<String, List<String>> equivalentCols = hg.getInverseEquivalenceMapping().get(variable);
                                List<String> equivalentCols = hg.getInverseEquivalenceMapping().get(variable).get(tableName);
                                for (int i = 0; i < equivalentCols.size() - 1; i++) {
                                    String cur = equivalentCols.get(i);
                                    String next = equivalentCols.get(i + 1);

                                    whereConditions.add(String.format("%s = %s", cur, next));
                                }
                            }

                            aliasedTables.add(String.format("(SELECT %s FROM %s WHERE %s) %s", String.join(", ", columnRewrites),
                                    tableName, String.join(" AND ", whereConditions), tableName));
                        } else {
                            aliasedTables.add(String.format("(SELECT %s FROM %s) %s", String.join(", ", columnRewrites), tableName, tableName));
                        }
                    }

                    fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(1));
                    tempTables.add(node.getIdentifier(1));
                    fnStr += String.format("AS SELECT DISTINCT * FROM %s;\n", String.join(" NATURAL INNER JOIN ", aliasedTables));
                }
            }
        }


        /**
         * Stage 2 - semi joins upwards
         *
         * Create a view for each join tree node from the second one upwards.
         * Join the tables in the node
         */
        //fnStr += "-- STAGE 2\n";

        for (int i = joinLayers.size() - 2; i >= 0; i--) {
            Set<JoinTreeNode> layer = joinLayers.get(i);
            //fnStr += "-- layer " + i + "\n";

            for (JoinTreeNode node : layer) {
                fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(2));
                tempTables.add(node.getIdentifier(2));
                fnStr += String.format("AS SELECT *\n");
                fnStr += String.format("FROM %s\n", node.getIdentifier(1));
                List<String> semiJoins = new LinkedList<>();
                for (JoinTreeNode child : node.getSuccessors()) {
                    String childName = child.getIdentifier(1);
                    HashSet<String> sameNameColumns = new HashSet<>(node.getAttributes());
                    HashSet<String> childCols = new HashSet<>(child.getAttributes());
                    // Perform set intersection
                    sameNameColumns.retainAll(childCols);

                    // Construct semi join conditions for WHERE statement
                    List<String> semiJoinConditions = new LinkedList<>();
                    for (String columnName : sameNameColumns) {
                        semiJoinConditions.add(String.format("(%s.%s = %s.%s)",
                                node.getIdentifier(1), columnName,
                                childName, columnName));
                    }

                    // EXISTS is the only choice because IN does not support multiple columns
                    if (!semiJoinConditions.isEmpty()) {
                        semiJoins.add(String.format("(EXISTS (SELECT * FROM %s WHERE %s))", childName, String.join(" AND ", semiJoinConditions)));
                    }
                }
                if (!semiJoins.isEmpty()) {
                    // There are nodes without any children
                    fnStr += String.format("WHERE %s;\n", String.join(" AND ", semiJoins));
                } else {
                    fnStr += ";\n";
                }
            }
        }

        // Create views for the last layer (which are just an alias for the views from stage 1)
        for (JoinTreeNode node : joinLayers.get(joinLayers.size() - 1)) {
            fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(2));
            tempTables.add(node.getIdentifier(2));
            fnStr += String.format("AS SELECT * FROM %s;\n", node.getIdentifier(1));
        }

        // Stage 3 - semi joins downwards
        //fnStr += "-- STAGE 3\n";

        for (int i = 1; i < joinLayers.size(); i++) {
            Set<JoinTreeNode> layer = joinLayers.get(i);
            //fnStr += "-- layer " + i + "\n";

            for (JoinTreeNode node : layer) {
                JoinTreeNode parent = node.getPredecessor();

                String parentName = parent.getIdentifier(2);
                HashSet<String> sameNameColumns = new HashSet<>(node.getAttributes());
                HashSet<String> childCols = new HashSet<>(parent.getAttributes());
                // Perform set intersection
                sameNameColumns.retainAll(childCols);

                List<String> semiJoinConditions = new LinkedList<>();
                for (String columnName : sameNameColumns) {
                    semiJoinConditions.add(String.format("(%s.%s = %s.%s)",
                            node.getIdentifier(2), columnName,
                            parentName, columnName));
                }

                fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(3));
                tempTables.add(node.getIdentifier(3));
                fnStr += String.format("AS SELECT *\n");
                fnStr += String.format("FROM %s\n", node.getIdentifier(2));
                if (!semiJoinConditions.isEmpty()) {
                    fnStr += String.format("WHERE EXISTS (SELECT * FROM %s WHERE %s);\n",
                            parent.getIdentifier(2), String.join(" AND ", semiJoinConditions));
                } else {
                    fnStr += ";";
                }
            }
        }

        // Create one view for the root node
        fnStr += String.format("CREATE TEMP TABLE %s\n", joinTree.getIdentifier(3));
        tempTables.add(joinTree.getIdentifier(3));
        fnStr += String.format("AS SELECT * FROM %s;\n", joinTree.getIdentifier(2));


        // Stage 4 - join everything

        //fnStr += "-- STAGE 4\n";

        List<String> allStage3Tables = new LinkedList<>();
        for (Set<JoinTreeNode> layer : joinLayers) {
            for (JoinTreeNode node : layer) {
                allStage3Tables.add(node.getIdentifier(3));
            }
        }

        fnStr += String.format("RETURN QUERY SELECT %s\n",
                resultColumns.stream().map(Column::getName).collect(Collectors.joining(", ")));

        fnStr += String.format("FROM %s;\n", String.join(" NATURAL INNER JOIN ", allStage3Tables));

//        for (String view : tempViews) {
//            fnStr += String.format("DROP VIEW %s;\n", view);
//        }
        for (String table : tempTables) {
            fnStr += String.format("DROP TABLE %s;\n", table);
        }

        fnStr += "END;\n";
        fnStr += "$$ LANGUAGE plpgsql;\n";

        return fnStr;
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

        result.setColumnToVariableMapping(equivalenceMapping);

        return result;
    }

    public List<String> getProjectColumns() {
        return projectColumns;
    }

    public Hypergraph getHypergraph() {
        return hypergraph;
    }

    public JoinTreeNode getJoinTree() {
        return joinTree;
    }
}
