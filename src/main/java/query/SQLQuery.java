package query;

import at.ac.tuwien.dbai.hgtools.hypergraph.Edge;
import at.ac.tuwien.dbai.hgtools.sql2hg.*;
import exceptions.JoinTreeGenerationException;
import exceptions.QueryConversionException;
import exceptions.TableNotFoundException;
import hypergraph.DecompositionOptions;
import hypergraph.Hyperedge;
import hypergraph.Hypergraph;
import hypergraph.WeightedHypergraph;
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
import schema.TableStatistics;

import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an SQL query over a database schema. Per default, statistical information is not used.
 * To consider statistics and compute a weighted decomposition, call setStatistics
 */
public class SQLQuery {
    private String query;
    private Schema schema;
    private DBSchema dbSchema;
    private String queryIdentifier;
    private DecompositionOptions decompositionOptions;
    private String tablespace = null;
    // Hypertree node -> column name
    private Map<String, Column> columnByNameMap;
    // List of columns to project in the end (or *)
    private List<String> projectColumns;
    // A set of all involved (alias) tables
    private Set<String> aliasTables;
    // Table aliases
    private Map<String, String> tableAliases;
    // Column aliases
    private Map<String, String> columnAliases;
    private Statement stmt;
    private Map<String, TableStatistics> statistics;

    private Hypergraph hypergraph;
    private JoinTreeNode joinTree;

    private Connection connection;

    GeneralQueryFinder hgFinder;

    private long joinTreeGenerationRuntime;
    private long hypergraphGenerationRuntime;

    private boolean createIndexes = false;

    private boolean applyAggregates = true;
    List<String> finalProjectAggregates = new LinkedList<>();
    Map<String, List<String>> whereFilters = new HashMap<>();

    public SQLQuery(String query, DBSchema dbSchema) throws QueryConversionException, TableNotFoundException {
        this.query = query;
        this.schema = dbSchema.toSchema();
        this.dbSchema = dbSchema;
        findProjectColumnsAndAliases();
        buildColumnLookup();

        decompositionOptions = new DecompositionOptions();
        queryIdentifier = UUID.randomUUID().toString().replace("-", "");
    }

    public void setStatistics(Map<String, TableStatistics> statistics) {
        this.statistics = statistics;
    }

    /**
     * @return a unique function name
     */
    public static String generateFunctionName() {
        return "htqo_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Can be used to alternatively read in schema creation statements
     *
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

    public void setDecompositionOptions(DecompositionOptions decompositionOptions) {
        this.decompositionOptions = decompositionOptions;
    }

    private String getNodeIdentifier(JoinTreeNode node, int stage) {
        return node.getIdentifier(stage) +  "_" + queryIdentifier.substring(0, 6);
    }

    private void findProjectColumnsAndAliases() throws QueryConversionException, TableNotFoundException {
        try {
            System.out.println("parsing query: " + query);
            stmt = CCJSqlParserUtil.parse(query);

            System.out.println("stmt: " + stmt);

            SQLQueryParser queryParser = new SQLQueryParser(stmt, dbSchema);

            tableAliases = queryParser.getAliases();
            Map<String, String> tableAliasesWithoutQuotes = new HashMap<>();
            for (String key : tableAliases.keySet()) {
                tableAliasesWithoutQuotes.put(key.replace("\"", ""),
                        tableAliases.get(key).replace("\"", ""));
            }

            tableAliases = tableAliasesWithoutQuotes;

            System.out.println("tableAliases: " + tableAliases);

            // Make sure that the project columns are fully quantified
            projectColumns = new LinkedList<>();
            for (String column: queryParser.getProjectColumns()) {
                if (!column.contains(".")) {
                    boolean relationFound = false;
                    for (Table table : dbSchema.getTables()) {
                        for (Column col : table.getColumns()) {
                            if (col.getName().equals(column)) {
                                relationFound = true;
                            }
                        }
                        if (relationFound) {
                            projectColumns.add(table.getName() + "." + column);
                            break;
                        }
                    }

                    if (!relationFound) {
                        throw new RuntimeException("There is no relation with a column named " + column);
                    }
                }
                else {
                    projectColumns.add(column);
                }
            }

            System.out.println("project columns: " + projectColumns);

            aliasTables = queryParser.getTables().stream()
                    .map(tableName -> tableName.replace("\"", ""))
                    .collect(Collectors.toSet());

            // Fill all column aliases: e.g. renamed.a -> original.a
            columnAliases = new HashMap<>();
            for (String aliasTableName : aliasTables) {
                String realTableName = tableAliases.get(aliasTableName);

                Table realTable = dbSchema.getTableByName(realTableName);
                if (realTable == null) {
                    throw new TableNotFoundException(String.format("Table %s not found in database", realTableName));
                }
                for (Column c : realTable.getColumns()) {
                    columnAliases.put(aliasTableName + "." + c.getName(), realTableName + "." + c.getName());
                }
            }
            System.out.println(columnAliases);
        } catch (JSQLParserException e) {
            throw new QueryConversionException("Error parsing SQL statement: " + e.getMessage() + Arrays.toString(e.getStackTrace()));
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

    public ParallelQueryExecution toParallelExecution() throws QueryConversionException {
        return toParallelExecution(false);
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public List<ParallelQueryExecution> enumerateParallelExecutions(boolean bcq, boolean enumerateRerootings) throws QueryConversionException, IOException {
        List<JoinTreeNode> joinTrees;
        List<ParallelQueryExecution> queryExecutions = new LinkedList<>();

        Hypergraph hg;
        if (statistics == null) {
            hg = toHypergraph();
        }
        else {
            try {
                // Fails when columns are "joined" inside the table
                long startTime = System.currentTimeMillis();
                hg = toWeightedHypergraph();

                this.hypergraphGenerationRuntime = System.currentTimeMillis() - startTime;
            }
            catch (IllegalArgumentException e) {
                throw new QueryConversionException(e.getMessage(), e);
            }
        }

        try {
            long startTime = System.currentTimeMillis();
            joinTrees = hg.enumerateJoinTrees(decompositionOptions, enumerateRerootings);
            this.joinTreeGenerationRuntime = System.currentTimeMillis() - startTime;
        } catch (JoinTreeGenerationException e) {
            throw new QueryConversionException("Error generating join tree: " + e.getMessage());
        }

        List<ParallelQueryExecution> executions = new LinkedList<>();

        for (JoinTreeNode joinTreeNode : joinTrees) {
            System.out.println(joinTreeNode);
            queryExecutions.add(toParallelExecution(hg, joinTreeNode, bcq));
        }

        return queryExecutions;
    }

    public ParallelQueryExecution toParallelExecution(boolean bcq) throws QueryConversionException {
        System.out.println("toParallelExecution");

        Hypergraph hg;
        if (statistics == null) {
            hg = toHypergraph();
        }
        else {
            try {
                // Fails when columns are "joined" inside the table
                long startTime = System.currentTimeMillis();
                hg = toWeightedHypergraph();

                this.hypergraphGenerationRuntime = System.currentTimeMillis() - startTime;
            }
            catch (IllegalArgumentException e) {
                throw new QueryConversionException(e.getMessage(), e);
            }
        }

        try {
            long startTime = System.currentTimeMillis();
            joinTree = hg.toJoinTree(decompositionOptions);
            this.joinTreeGenerationRuntime = System.currentTimeMillis() - startTime;
        } catch (JoinTreeGenerationException e) {
            throw new QueryConversionException("Error generating join tree: " + e.getMessage());
        }

        return toParallelExecution(hg, joinTree, bcq);
    }

    public ParallelQueryExecution toParallelExecution(Hypergraph hg, JoinTreeNode joinTree, boolean bcq) throws QueryConversionException {
        List<List<List<String>>> resultQueryStages = new LinkedList<>();
        String finalTableName = "htqo_" + UUID.randomUUID().toString().replace("-", "");
        System.out.println("tablespace: " + tablespace);

        DropStatements dropStatements = new DropStatements();

        List<Column> resultColumns = new LinkedList<>();

        // Check if there is a select * or select [specific columns]
        if (projectColumns.size() == 1 && projectColumns.get(0).equals("*")) {
            // Go through all hypergraph vertices
            for (String node : hg.getVertices()) {
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
            //System.out.println("column aliases: " + columnAliases);
            for (String projectCol : projectColumns) {
                //System.out.println("project col: " + projectCol);
                //System.out.println("project col 2: " + columnAliases.get(projectCol));

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
        joinTree.projectAllColumns(resultColumns.stream()
                .map(Column::getName)
                .collect(Collectors.toSet()), hg);

        System.out.println("Reduced attribute join tree: " + joinTree);

        // Set up a lookup table of tables for quickly getting the variables of a hyperedge
        Map<String, Table> tablesByName = new HashMap<>();
        for (Table t : dbSchema.getTables()) {
            tablesByName.put(t.getName(), t);
        }

        List<Set<JoinTreeNode>> joinLayers = joinTree.getLayers();

        /**
         * Stage 1 - join tables inside tree nodes
         *
         * Create a view for each join tree node.
         * Join the tables in the node.
         * This step is redundant with hypertree width 1 and could be optimized away (if it has any significant overhead)
         */

        List<String> stage1 = new LinkedList<>();

        for (Set<JoinTreeNode> layer : joinLayers) {
            for (JoinTreeNode node : layer) {
                LinkedList<String> aliasedTables = new LinkedList<>();

                for (String tableAliasName : node.getTables()) {
                    // Very important distinction: tableName is the DB table name, tableAliasName is the potentially
                    // renamed table name. Multiple aliases may point to the same table.
                    // The alias may also be equivalent to the table if no renaming has occurred
                    String tableName = tableAliases.get(tableAliasName);
                    LinkedList<String> columnRewrites = new LinkedList<>();
                    LinkedList<String> filteredColumnRewrites = new LinkedList<>();

                    Hyperedge he = hg.getEdgeByName(tableAliasName);
                    Set<String> tableVariables = new HashSet<>(node.getAttributes());
                    tableVariables.retainAll(he.getNodes());
                    Set<String> tableVariablesIncludingFiltered = new HashSet<>(tableVariables);
                    for (String whereFilterAttribute : whereFilters.keySet()) {
                        if (he.getNodes().contains(whereFilterAttribute)) {
                            tableVariablesIncludingFiltered.add(whereFilterAttribute);
                        }
                    }

                    for (String variableName : tableVariables) {
                        // Get only the first as any of the equivalent is sufficient
                        String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableAliasName).get(0);
                        columnRewrites.add(String.format("%s AS %s", columnIdentifier, variableName));
                    }
                    for (String variableName : tableVariablesIncludingFiltered) {
                        String columnIdentifier = hg.getInverseEquivalenceMapping().get(variableName).get(tableAliasName).get(0);
                        filteredColumnRewrites.add(String.format("%s AS %s", columnIdentifier, variableName));
                    }
                    // TODO handle case of cross-product and no columns from that table selected

                    // Check if there are more variables than columns -> Some columns are equivalent and
                    // a filter checking this needs to be added
                    if (he.getNodes().size() < tablesByName.get(tableName).getColumns().size()) {
                        List<String> whereConditions = new LinkedList<>();
                        for (String variable : he.getNodes()) {
                            List<String> equivalentCols = hg.getInverseEquivalenceMapping().get(variable).get(tableName);
                            for (int i = 0; i < equivalentCols.size() - 1; i++) {
                                String cur = equivalentCols.get(i);
                                String next = equivalentCols.get(i + 1);

                                whereConditions.add(String.format("%s = %s", cur, next));
                            }
                        }

                        // Rename the table to the table alias
                        aliasedTables.add(String.format("(SELECT %s FROM %s WHERE %s) %s",
                                String.join(", ", columnRewrites.size() > 0 ? columnRewrites : List.of("1")), // If there are no selections, select 1
                                tableName,
                                String.join(" AND ", whereConditions),
                                tableAliasName));
                        // TODO also add support for where filters inside if block
                    } else {
                        List<String> whereConditions = new LinkedList<>();

                        System.out.println("attributes:  " + node.getAttributes());
                        for (String attr : tableVariablesIncludingFiltered) {
                            if (whereFilters.containsKey(attr)) {
                                whereConditions.addAll(whereFilters.get(attr));
                            }
                        }
                        System.out.println("where conditions: " + whereConditions);

                        if (whereConditions.isEmpty()) {
                            String baseViewQuery = String.format("(SELECT %s FROM %s) %s",
                                    String.join(", ", columnRewrites.size() > 0 ? columnRewrites : List.of("1")),
                                    tableName, tableAliasName);
                            aliasedTables.add(baseViewQuery);
                        } else { // DISTINCT
                            String baseViewQuery = String.format("(SELECT %s FROM %s) sq",
                                    String.join(", ", filteredColumnRewrites.size() > 0 ? filteredColumnRewrites : List.of("1")),
                                    tableName);
                            aliasedTables.add(String.format("(SELECT * FROM %s WHERE %s) %s", baseViewQuery,
                                    whereConditions.stream().collect(Collectors.joining(" AND ")), tableAliasName));
                        }
                    }
                }

                String sqlStatement = "";

                if (aliasedTables.size() == 1) {
                    sqlStatement += String.format("CREATE VIEW %s\n", getNodeIdentifier(node, 1));
                    //sqlStatement += String.format("CREATE TEMPORARY TABLE %s\n", getNodeIdentifier(node, 1));
                    if (!stage1.contains(sqlStatement)) {
                        dropStatements.dropView(getNodeIdentifier(node, 1));
                    }
                } else {
                    sqlStatement += String.format("CREATE UNLOGGED TABLE %s %s\n",
                            getNodeIdentifier(node, 1),
                            tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                    if (!stage1.contains(sqlStatement)) {
                        dropStatements.dropTable(getNodeIdentifier(node, 1));
                    }
                }

                sqlStatement += String.format("AS SELECT DISTINCT %s FROM %s;\n",
                        String.join(", ", node.getAttributes()),
                        String.join(" NATURAL INNER JOIN ", aliasedTables));

                // Sometimes parts of the tree are duplicated
                if (!stage1.contains(sqlStatement)) {
                    stage1.add(sqlStatement);
                }
            }
        }

        resultQueryStages.add(List.of(stage1));

        /**
         * Stage 2 - semi joins upwards
         *
         * Create a view for each join tree node from the second one upwards.
         * Join the tables in the node
         */

        List<List<String>> stage2 = new LinkedList<>();
        for (int i = joinLayers.size() - 2; i >= 0; i--) {
            Set<JoinTreeNode> layer = joinLayers.get(i);

            List<String> layerStatements = new LinkedList<>();
            List<String> layerCreateIndexStatements = new LinkedList<>();
            for (JoinTreeNode node : layer) {
                List<String> semiJoins = new LinkedList<>();
                for (JoinTreeNode child : node.getSuccessors()) {
                    // If the child node is a leaf, use the result from stage 1, else stage 2 was computed and use that
                    String childName;
                    if (child.isLeaf()) {
                        childName = getNodeIdentifier(child, 1);
                    }
                    else {
                        childName = getNodeIdentifier(child, 2);
                    }
                    HashSet<String> sameNameColumns = new HashSet<>(node.getAttributes());
                    HashSet<String> childCols = new HashSet<>(child.getAttributes());
                    // Perform set intersection
                    sameNameColumns.retainAll(childCols);

                    // Construct semi join conditions for WHERE statement
                    List<String> semiJoinConditions = new LinkedList<>();
                    for (String columnName : sameNameColumns) {
                        semiJoinConditions.add(String.format("(%s.%s = %s.%s)",
                                getNodeIdentifier(node, 1), columnName,
                                childName, columnName));
                    }

                    // EXISTS is the only choice because IN does not support multiple columns
                    if (!semiJoinConditions.isEmpty()) {
                        semiJoins.add(String.format("(EXISTS (SELECT 1 FROM %s WHERE %s))", childName, String.join(" AND ", semiJoinConditions)));
                    }
                }

                String sqlStatement = "";

                if (!semiJoins.isEmpty()) {
                    sqlStatement += String.format("CREATE UNLOGGED TABLE %s %s\n",
                            getNodeIdentifier(node, 2),
                            tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                    dropStatements.dropTable(getNodeIdentifier(node, 2));
                    sqlStatement += String.format("AS SELECT  *\n");
                    sqlStatement += String.format("FROM %s\n", getNodeIdentifier(node, 1));
                    sqlStatement += String.format("WHERE %s;\n", String.join(" AND ", semiJoins));

                    for (String attr : node.getAttributes()) {
                        layerCreateIndexStatements.add(String.format("CREATE INDEX idx_%s_%s ON %s (%s)",
                                getNodeIdentifier(node, 2), attr, getNodeIdentifier(node, 2), attr));
                    }
                } else {
                    // If there are no semi joins, just create a view to avoid unnecessary copying
                    sqlStatement += String.format("CREATE VIEW %s\n", getNodeIdentifier(node, 2));
                    dropStatements.dropView(getNodeIdentifier(node, 2));
                    sqlStatement += String.format("AS SELECT *\n");
                    sqlStatement += String.format("FROM %s;\n", getNodeIdentifier(node, 1));
                }

                layerStatements.add(sqlStatement);
            }
            stage2.add(layerStatements);
            if (!layerCreateIndexStatements.isEmpty() && createIndexes) {
                stage2.add(layerCreateIndexStatements);
            }
        }

        List<String> aliasViews = new LinkedList<>();
        // Create views for the last layer (which are just an alias for the views from stage 1)
        for (JoinTreeNode node : joinLayers.get(joinLayers.size() - 1)) {
            String createStatement = String.format("CREATE VIEW %s\n", getNodeIdentifier(node, 2))
            + String.format("AS SELECT * FROM %s;\n", getNodeIdentifier(node, 1));
            aliasViews.add(createStatement);
            dropStatements.dropView(getNodeIdentifier(node, 2));
        }

        stage2.add(aliasViews);
        resultQueryStages.add(stage2);

        List<List<String>> stage3 = new LinkedList<>();

        if (!bcq) {
            // Stage 3 - semi joins downwards

            for (int i = 1; i < joinLayers.size(); i++) {
                Set<JoinTreeNode> layer = joinLayers.get(i);
                List<String> layerStatements = new LinkedList<>();

                for (JoinTreeNode node : layer) {
                    JoinTreeNode parent = node.getPredecessor();

                    //System.out.println("node: " + node);
                    //System.out.println("parent: " + parent);
                    String parentName;
                    if (parent.getPredecessor() == null) {
                        parentName = getNodeIdentifier(parent, 2);
                    }
                    else {
                        parentName = getNodeIdentifier(parent, 3);
                    }
                    HashSet<String> sameNameColumns = new HashSet<>(node.getAttributes());
                    HashSet<String> childCols = new HashSet<>(parent.getAttributes());
                    // Perform set intersection
                    sameNameColumns.retainAll(childCols);

                    List<String> semiJoinConditions = new LinkedList<>();
                    for (String columnName : sameNameColumns) {
                        semiJoinConditions.add(String.format("(%s.%s = %s.%s)",
                                getNodeIdentifier(node, 2), columnName,
                                parentName, columnName));
                    }

                    String sqlStatement = "";

                    if (!semiJoinConditions.isEmpty()) {
                        sqlStatement += String.format("CREATE UNLOGGED TABLE %s %s\n",
                                getNodeIdentifier(node, 3),
                                tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                        dropStatements.dropTable(getNodeIdentifier(node, 3));
                    } else {
                        sqlStatement += String.format("CREATE VIEW %s\n", getNodeIdentifier(node, 3));
                        dropStatements.dropView(getNodeIdentifier(node, 3));
                    }
                    sqlStatement += String.format("AS SELECT *\n");
                    sqlStatement += String.format("FROM %s\n", getNodeIdentifier(node, 2));
                    if (!semiJoinConditions.isEmpty()) {
                        sqlStatement += String.format("WHERE EXISTS (SELECT 1 FROM %s WHERE %s);\n",
                                parentName, String.join(" AND ", semiJoinConditions));
                    } else {
                        sqlStatement += ";";
                    }
                    layerStatements.add(sqlStatement);
                }
                stage3.add(layerStatements);
            }

            String topStatement = "";

            // Create one view for the root node
            topStatement += String.format("CREATE VIEW %s\n", getNodeIdentifier(joinTree, 3));
            //topStatement += String.format("AS SELECT * FROM %s;\n", getNodeIdentifier(joinTree, 2));
            topStatement += String.format("AS SELECT * FROM %s;\n", getNodeIdentifier(joinTree, 2));
            dropStatements.dropView(getNodeIdentifier(joinTree, 3));

            stage3.add(List.of(topStatement));

            resultQueryStages.add(stage3);

            // Stage 4 - join everything

            List<String> allStage3Tables = new LinkedList<>();
            for (Set<JoinTreeNode> layer : joinLayers) {
                for (JoinTreeNode node : layer) {
                    allStage3Tables.add(getNodeIdentifier(node, 3));
                }
            }

             // TODO add cli argument
            boolean parallelizeJoinSplit = false;

            System.out.println(joinTree.getFinalJoinSplit());
            List<List<JoinTreeNode>> joinSplit = joinTree.getFinalJoinSplit();

            List<String> groupNames = new LinkedList<>();
            if (!parallelizeJoinSplit) {
                int groupIndex = 1;
                for (List<JoinTreeNode> group : joinSplit) {
                    String groupName = "htqo_group_" + groupIndex + UUID.randomUUID().toString().replace("-", "");
                    String groupQuery = String.format("CREATE UNLOGGED TABLE %s AS SELECT *\n", groupName);
                    groupQuery += String.format("FROM %s;\n",
                            group.stream().map(node -> getNodeIdentifier(node, 3))
                                    .collect(Collectors.joining(" NATURAL INNER JOIN ")));
                    groupNames.add(groupName);
                    resultQueryStages.add(List.of(List.of(groupQuery)));
                    dropStatements.dropTable(groupName);
                    groupIndex++;
                }
            }
            else {
                // TODO implement
            }

//            String finalQuery = String.format("CREATE VIEW %s AS SELECT %s\n", finalTableName,
//                    applyAggregates ? String.join(", ", finalProjectAggregates)
//                                        : resultColumns.stream().map(Column::getName).collect(Collectors.joining(", ")));
//            finalQuery += String.format("FROM %s;\n", String.join(" NATURAL INNER JOIN ", allStage3Tables));
//            dropStatements.dropView(finalTableName);
//
//            resultQueryStages.add(List.of(List.of(finalQuery)));

            String finalQuery = String.format("CREATE VIEW %s AS SELECT %s\n", finalTableName,
                    applyAggregates ? String.join(", ", finalProjectAggregates)
                            : resultColumns.stream().map(Column::getName).collect(Collectors.joining(", ")));
            finalQuery += String.format("FROM %s;\n", String.join(" NATURAL INNER JOIN ", groupNames));
            dropStatements.dropView(finalTableName);

            resultQueryStages.add(List.of(List.of(finalQuery)));
        }
        else {
            // If only a BCQ is needed, create a VIEW alias of the last join tree node

            // Since it is the top layer, there can only be one node (the root of the tree)
            JoinTreeNode topNode = (JoinTreeNode) joinLayers.get(0).toArray()[0];
            String finalQuery = String.format("CREATE VIEW %s AS SELECT * FROM %s\n",
                    finalTableName, getNodeIdentifier(topNode, 2));
            dropStatements.dropView(finalTableName);
            resultQueryStages.add(List.of(List.of(finalQuery)));
        }

        ParallelQueryExecution result =
                new ParallelQueryExecution(resultQueryStages, dropStatements, resultColumns, finalTableName);
        result.setHypergraph(hypergraph);
        result.setJoinTree(joinTree);

        return result;
    }

    // TODO refactor to use the above method
    public String toFunction(String functionName) throws QueryConversionException {
        System.out.println("toFunction called");
        Hypergraph hg;
        if (statistics == null) {
            hg = toHypergraph();
        }
        else {
            try {
                // Fails when columns are "joined" inside the table
                hg = toWeightedHypergraph();
            }
            catch (IllegalArgumentException e) {
                throw new QueryConversionException(e.getMessage(), e);
            }
        }
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
            for (String node : hg.getVertices()) {
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

        System.out.println("Output columns: " + resultColumns);

        // Transform project column names to hypergraph variable names
        joinTree.projectAllColumns(resultColumns.stream()
                .map(Column::getName)
                .collect(Collectors.toSet()), hg);

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
                    if (he.getNodes().size() < tablesByName.get(tableName).getColumns().size()) {
                        List<String> whereConditions = new LinkedList<>();
                        for (String variable : he.getNodes()) {
                            List<String> equivalentCols = hg.getInverseEquivalenceMapping().get(variable).get(tableName);
                            for (int i = 0; i < equivalentCols.size() - 1; i++) {
                                String cur = equivalentCols.get(i);
                                String next = equivalentCols.get(i + 1);

                                whereConditions.add(String.format("%s = %s", cur, next));
                            }
                        }

                        // Rename the table to the table alias
                        aliasedTables.add(String.format("(SELECT %s FROM %s WHERE %s) %s",
                                String.join(", ", columnRewrites),
                                tableName,
                                String.join(" AND ", whereConditions),
                                tableAliasName));
                    } else {
                        aliasedTables.add(String.format("(SELECT %s FROM %s) %s", String.join(", ", columnRewrites), tableName, tableAliasName));
                    }
                }

                if (aliasedTables.size() == 1) {
                    fnStr += String.format("CREATE VIEW %s\n", node.getIdentifier(1));
                    dropStatements.dropView(node.getIdentifier(1));
                } else {
                    fnStr += String.format("CREATE UNLOGGED TABLE %s %s\n", node.getIdentifier(1),
                            tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                    dropStatements.dropTable(node.getIdentifier(1));
                }

                fnStr += String.format("AS SELECT %s FROM %s;\n",
                        String.join(", ", node.getAttributes()),
                        String.join(" NATURAL INNER JOIN ", aliasedTables));
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
                    String childName;
                    if (child.isLeaf()) {
                        childName = child.getIdentifier(1);
                    }
                    else {
                        childName = child.getIdentifier(2);
                    }
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
                    fnStr += String.format("CREATE UNLOGGED TABLE %s %s\n", node.getIdentifier(2),
                            tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                    dropStatements.dropTable(node.getIdentifier(2));
                    fnStr += String.format("AS SELECT *\n");
                    fnStr += String.format("FROM %s\n", node.getIdentifier(1));
                    fnStr += String.format("WHERE %s;\n", String.join(" AND ", semiJoins));
                } else {
                    // If there are no semi joins, just create a view to avoid unnecessary copying
                    fnStr += String.format("CREATE VIEW %s\n", node.getIdentifier(2));
                    dropStatements.dropView(node.getIdentifier(2));
                    fnStr += String.format("AS SELECT *\n");
                    fnStr += String.format("FROM %s;\n", node.getIdentifier(1));
                }
            }
        }

        // Create views for the last layer (which are just an alias for the views from stage 1)
        for (JoinTreeNode node : joinLayers.get(joinLayers.size() - 1)) {
            fnStr += String.format("CREATE VIEW %s\n", node.getIdentifier(2));
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

                String parentName;
                if (parent.getPredecessor() == null) {
                    parentName = parent.getIdentifier(2);
                }
                else {
                    parentName = parent.getIdentifier(3);
                }
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
                    fnStr += String.format("CREATE UNLOGGED TABLE %s %s\n", node.getIdentifier(3),
                            tablespace == null ? "" : String.format("TABLESPACE %s", tablespace));
                    dropStatements.dropTable(node.getIdentifier(3));
                } else {
                    fnStr += String.format("CREATE VIEW %s\n", node.getIdentifier(3));
                    dropStatements.dropView(node.getIdentifier(3));
                }
                fnStr += String.format("AS SELECT *\n");
                fnStr += String.format("FROM %s\n", node.getIdentifier(2));
                if (!semiJoinConditions.isEmpty()) {
                    fnStr += String.format("WHERE EXISTS (SELECT * FROM %s WHERE %s);\n",
                            parentName, String.join(" AND ", semiJoinConditions));
                } else {
                    fnStr += ";";
                }
            }
        }

        // Create one view for the root node
        fnStr += String.format("CREATE VIEW %s\n", joinTree.getIdentifier(3));
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

        System.out.println(fnStr);

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
        hgFinder = new GeneralQueryFinder(schema);
        hgFinder.run(selectStmt);

        System.out.println("tables: " + hgFinder.getTables());
        System.out.println("joins: " + hgFinder.getJoins());
        HypergraphBuilder hgBuilder = new HypergraphBuilder();
        for (Predicate table : hgFinder.getTables()) {
            hgBuilder.buildEdge(table);
        }
        for (Predicate table : hgFinder.getTables()) {
            if (table instanceof ViewPredicate) {
                ViewPredicate view = (ViewPredicate) table;
                for (Equality join : view.getJoins()) {
                    hgBuilder.buildViewJoin(view.getAlias(), join);
                }
            }
        }
        for (Equality join : hgFinder.getJoins()) {
            System.out.println(join);
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

        finalProjectAggregates = hgFinder.getSelects().stream().map(aggregate -> {
            String aggregateColumnName = aggregate.split(";")[0];
            String replacedAggregate = aggregate.split(";")[1];
            for (String columnName : result.getColumnToVariableMapping().keySet()) {
                if (aggregateColumnName.equals(columnName)) {
                    replacedAggregate = replacedAggregate.replace(columnName, result.getColumnToVariableMapping().get(columnName));
                }
            }
            return replacedAggregate;
        }).collect(Collectors.toList());

        for (String line : hgFinder.getFilters()) {
            String[] splits = line.split(";");

            String vertexName = result.getColumnToVariableMapping().get(splits[0]);
            String whereCondition = splits[splits.length - 1].replace(splits[0], vertexName);

            if (whereFilters.containsKey(vertexName)) {
                whereFilters.get(vertexName).add(whereCondition);
            }
            else {
                whereFilters.put(vertexName, new LinkedList<>(List.of(whereCondition)));
            }
        }

        System.out.println("finalProjectAggregates: " + finalProjectAggregates);
        System.out.println("whereFilters: " + whereFilters);

        return result;
    }

    public WeightedHypergraph toWeightedHypergraph() throws QueryConversionException {
        WeightedHypergraph weightedHypergraph = new WeightedHypergraph(toHypergraph(), statistics, tableAliases);
        weightedHypergraph.useConnection(connection);
        weightedHypergraph.setWhereFilters(whereFilters);

        return weightedHypergraph;
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

    public Set<String> getAliasTables() {
        return aliasTables;
    }

    public Set<String> getRealTables() {
        return aliasTables.stream().map(this::getRealTable).collect(Collectors.toSet());
    }

    public String getRealTable(String aliasName) {
        return tableAliases.get(aliasName);
    }

    public long getJoinTreeGenerationRuntime() {
        return joinTreeGenerationRuntime;
    }

    public long getHypergraphGenerationRuntime() {
        return hypergraphGenerationRuntime;
    }

    public void setCreateIndexes(boolean createIndexes) {
        this.createIndexes = createIndexes;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setApplyAggregates(boolean applyAggregates) {
        this.applyAggregates = applyAggregates;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }
}
