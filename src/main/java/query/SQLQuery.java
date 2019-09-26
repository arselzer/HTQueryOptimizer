package query;

import at.ac.tuwien.dbai.hgtools.hypergraph.Edge;
import at.ac.tuwien.dbai.hgtools.sql2hg.*;
import exceptions.JoinTreeGenerationException;
import exceptions.QueryConversionException;
import hypergraph.DecompositionOptions;
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
    private DecompositionOptions decompositionOptions;
    // Hypertree node -> column name
    private Map<String, Column> columnByNameMap;
    // List of columns to project in the end (or *)
    private List<String> projectColumns;
    // A set of all involved tables
    private Set<String> aliasTables;
    // Table aliases
    private Map<String, String> tableAliases;
    // Column aliases
    private Map<String, String> columnAliases;
    private Statement stmt;

    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;

    public SQLQuery(String query, DBSchema dbSchema) throws QueryConversionException {
        this.query = query;
        this.schema = dbSchema.toSchema();
        this.dbSchema = dbSchema;
        findProjectColumnsAndAliases();
        buildColumnLookup();

        decompositionOptions = new DecompositionOptions();
    }

    public void setDecompositionOptions(DecompositionOptions decompositionOptions) {
        this.decompositionOptions = decompositionOptions;
    }

    /**
     * Class to keep track of tables and views to drop (in the correct order)
     */
    private class DropStatements {
        private LinkedList<String> dropStrings = new LinkedList<>();

        public void dropTable(String name) {
            dropStrings.addFirst(String.format("DROP TABLE %s;", name));
        }
        public void dropView(String name) {
            dropStrings.addFirst(String.format("DROP VIEW %s;", name));
        }

        public String toString() {
            return String.join("\n", dropStrings);
        }
    }

    /**
     * @return a unique function name
     */
    public static String generateFunctionName() {
        return "htqo_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Can be used to alternatively read in schema creation statements
     * @param schemaString
     * @return
     * @throws QueryConversionException
     */
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

    private void findProjectColumnsAndAliases() throws QueryConversionException {
        try {
            stmt = CCJSqlParserUtil.parse(query);

            SQLQueryParser queryParser = new SQLQueryParser(stmt, dbSchema);
            projectColumns = queryParser.getProjectColumns();
            tableAliases = queryParser.getAliases();
            aliasTables = queryParser.getTables();

            // Fill all column aliases: e.g. renamed.a -> original.a
            columnAliases = new HashMap<>();
            for (String aliasTableName : aliasTables) {
                String realTableName = tableAliases.get(aliasTableName);
                Table realTable = dbSchema.getTableByName(realTableName);
                for (Column c : realTable.getColumns()) {
                    columnAliases.put(aliasTableName + "." + c.getName(), realTableName + "." + c.getName());
                }
            }
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
            joinTree = hg.toJoinTree(decompositionOptions);
        } catch (JoinTreeGenerationException e) {
            throw new QueryConversionException("Error generating join tree: " + e.getMessage());
        }

        DropStatements dropStatements = new DropStatements();

        String fnStr = "";
        fnStr += String.format("CREATE FUNCTION %s()\n", functionName);

        List<Column> resultColumns = new LinkedList<>();

        // Check if there is a select * or select [specific columns]
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
                Column realColumn = columnByNameMap.get(columnAliases.get(projectCol));
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

        // Transform project column names to hypergraph variable names
        joinTree.projectAllColumns(projectColumns.stream().map(col -> hg.getColumnToVariableMapping().get(col)).collect(Collectors.toSet()));

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
         * Stage 1 - join tables inside tree nodes
         *
         * Create a view for each join tree node.
         * Join the tables in the node.
         * This step is redundant with hypertree width 1 and could be optimized away (if it has any significant overhead)
         */

        for (Set<JoinTreeNode> layer : joinLayers) {
            for (JoinTreeNode node : layer) {
                LinkedList<String> aliasedTables = new LinkedList<>();

                for (String tableAliasName : node.getTables()) {
                    // Very important distinction: tableName is the DB table name, tableAliasName is the potentially
                    // renamed table name. Multiple aliases may point to the same table.
                    // The alias may also be equivalent to the table if no renaming has occurred
                    String tableName = tableAliases.get(tableAliasName);
                    LinkedList<String> columnRewrites = new LinkedList<>();

                    Hyperedge he = hg.getEdgeByName(tableAliasName);
                    for (String variableName : he.getNodes()) {
                        // Get only the first as any of the equivalent is sufficient
                        String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableAliasName).get(0);
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

                        // Rename the table to the table alias
                        aliasedTables.add(String.format("(SELECT %s FROM %s WHERE %s) %s", String.join(", ", columnRewrites),
                                tableName, String.join(" AND ", whereConditions), tableAliasName));
                    } else {
                        aliasedTables.add(String.format("(SELECT %s FROM %s) %s", String.join(", ", columnRewrites), tableName, tableAliasName));
                    }
                }

                if (aliasedTables.size() == 1) {
                    fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(1));
                    dropStatements.dropView(node.getIdentifier(1));
                } else {
                    fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(1));
                    dropStatements.dropTable(node.getIdentifier(1));
                }

                fnStr += String.format("AS SELECT %s FROM %s;\n", String.join(", ", node.getAttributes()), String.join(" NATURAL INNER JOIN ", aliasedTables));
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

            for (JoinTreeNode node : layer) {
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
                    fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(2));
                    dropStatements.dropTable(node.getIdentifier(2));
                    fnStr += String.format("AS SELECT *\n");
                    fnStr += String.format("FROM %s\n", node.getIdentifier(1));
                    fnStr += String.format("WHERE %s;\n", String.join(" AND ", semiJoins));
                } else {
                    // If there are no semi joins, just create a view to avoid unnecessary copying
                    fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(2));
                    dropStatements.dropView(node.getIdentifier(2));
                    fnStr += String.format("AS SELECT *\n");
                    fnStr += String.format("FROM %s;\n", node.getIdentifier(1));
                }
            }
        }

        // Create views for the last layer (which are just an alias for the views from stage 1)
        for (JoinTreeNode node : joinLayers.get(joinLayers.size() - 1)) {
            fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(2));
            dropStatements.dropView(node.getIdentifier(2));
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

                if (!semiJoinConditions.isEmpty()) {
                    fnStr += String.format("CREATE TEMP TABLE %s\n", node.getIdentifier(3));
                    dropStatements.dropTable(node.getIdentifier(3));
                }
                else {
                    fnStr += String.format("CREATE TEMP VIEW %s\n", node.getIdentifier(3));
                    dropStatements.dropView(node.getIdentifier(3));
                }
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
        fnStr += String.format("CREATE TEMP VIEW %s\n", joinTree.getIdentifier(3));
        dropStatements.dropView(joinTree.getIdentifier(3));
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

        fnStr += dropStatements.toString();

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
