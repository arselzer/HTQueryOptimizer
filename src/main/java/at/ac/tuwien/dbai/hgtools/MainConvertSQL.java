package at.ac.tuwien.dbai.hgtools;

import at.ac.tuwien.dbai.hgtools.hypergraph.Hypergraph;
import at.ac.tuwien.dbai.hgtools.sql2hg.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class MainConvertSQL {

    public static void main(String[] args) throws JSQLParserException, IOException {
        Schema schema = new Schema();
        String schemaString = readFile(args[0]);
        readPredicateDefinitions(schemaString, schema);

        for (int i = 1; i < args.length; i++) {
            File file = new File(args[i]);
            File[] files;
            if (file.isDirectory()) {
                files = file.listFiles();
            } else {
                files = new File[1];
                files[0] = file;
            }
            processFiles(files, schema);
        }
    }

    private static void processFiles(File[] files, Schema schema) throws JSQLParserException, IOException {
        for (File file : files) {
            if (file.isDirectory()) {
                // System.out.println("Directory: " + file.getName());
                processFiles(file.listFiles(), schema); // Calls same method again.
            } else if (file.getName().endsWith("sql") || file.getName().endsWith("tpl")) {
                /*
                 * TODO create a regex containing all possible extensions and then check if the
                 * name matches it
                 */
                String sqlString = readFile(file.getPath());
                Statement stmt = CCJSqlParserUtil.parse(sqlString);
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
                Hypergraph h = hgBuilder.getHypergraph();
                // System.out.println();
                System.out.println(hgBuilder.getVarToColMapping());
                System.out.println();
                System.out.println(h);

                /**
                 * String newFile = file.getPath(); newFile = "output/" + newFile.substring(0,
                 * newFile.lastIndexOf(".")) + ".hg"; Path newFilePath = Paths.get(newFile);
                 * Files.createDirectories(newFilePath.getParent()); if
                 * (!Files.exists(newFilePath)) Files.createFile(newFilePath);
                 * Files.write(Paths.get(newFile), H.toFile(), Charset.forName("UTF-8"));
                 */
            }
        }
    }

    private static void readPredicateDefinitions(String schemaString, Schema schema) throws JSQLParserException {
        Statements schemaStmts = CCJSqlParserUtil.parseStatements(schemaString);
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
                schema.addPredicateDefinition(new PredicateDefinition(predicateName, attributes));
            } catch (ClassCastException c) {
                System.err.println("\"" + schemaStmt + "\" is not a CREATE statement.");
            }
        }
    }

    public static String readFile(String fName) {
        String s = "";
        try (BufferedReader br = new BufferedReader(new FileReader(fName))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                if (!sCurrentLine.startsWith("--"))
                    s += sCurrentLine + " ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

}
