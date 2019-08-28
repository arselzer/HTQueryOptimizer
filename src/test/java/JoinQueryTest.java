import hypergraph.Hypergraph;
import org.junit.jupiter.api.Test;
import query.DBSchema;
import query.EquiJoinCondition;
import query.JoinQuery;
import query.Table;

import java.util.List;

class JoinQueryTest {
    @Test
    void generateHypertreeTriangleQuery() {
        // The query:
        /**
         * SELECT *
         * FROM t1, t2, t3, t4
         * WHERE t1.a = t2.a
         * AND t2.c = t3.a
         * AND t3.d = t1.b
         */
        Table t1 = new Table();
        Table t2 = new Table();
        Table t3 = new Table();
        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");

        t1.setColumns(List.of("a", "b"));
        t2.setColumns(List.of("a", "c"));
        t3.setColumns(List.of("a", "d"));


        DBSchema s = new DBSchema();
        s.setTables(List.of(t1, t2, t3));
        List<String> projectColumns = List.of("t1.a", "t2.a", "t3.a", "t1.b", "t2.c", "t3.d");
        List<EquiJoinCondition> joinConditions = List.of(new EquiJoinCondition("t1.a", "t2.a"),
                new EquiJoinCondition("t2.c", "t3.a"), new EquiJoinCondition("t3.d", "t1.b"));
        JoinQuery q = new JoinQuery(s, projectColumns, joinConditions);

        System.out.printf("hypergraph.Hypergraph: %s", q.toHypergraph());

        System.out.printf("DTL: %s", q.toHypergraph().toDTL());

    }

    @Test
    void generateStarQuery() {
        // The query:
        /**
         * SELECT *
         * FROM t1, t2, t3, t4
         * WHERE t1.a = t2.a
         * AND t1.a = t3.a
         * AND t1.a = t4.a
         * AND t4.e = t5.a
         */
        Table t1 = new Table();
        Table t2 = new Table();
        Table t3 = new Table();
        Table t4 = new Table();
        Table t5 = new Table();
        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t4.setName("t4");
        t5.setName("t5");

        t1.setColumns(List.of("a", "b"));
        t2.setColumns(List.of("a", "c"));
        t3.setColumns(List.of("a", "d"));
        t4.setColumns(List.of("a", "e"));
        t5.setColumns(List.of("a", "f"));


        DBSchema s = new DBSchema();
        s.setTables(List.of(t1, t2, t3, t4, t5));
        List<String> projectColumns = List.of("t1.a", "t2.a", "t3.a", "t1.b",
                "t2.c", "t3.d", "t4.a", "t4.e", "t5.a", "t5.f");
        List<EquiJoinCondition> joinConditions = List.of(
                new EquiJoinCondition("t1.a", "t2.a"),
                new EquiJoinCondition("t1.a", "t3.a"),
                new EquiJoinCondition("t1.a", "t4.a"),
                new EquiJoinCondition("t4.e", "t5.a"));
        JoinQuery q = new JoinQuery(s, projectColumns, joinConditions);

        System.out.printf("Hypergraph: %s", q.toHypergraph());

        System.out.printf("DTL: %s", q.toHypergraph().toDTL());
    }

    @Test
    void generateJoinTree() {
        // The query:
        /**
         * SELECT *
         * FROM t1, t2, t3, t4
         * WHERE t1.a = t2.a
         * AND t1.a = t3.a
         * AND t1.a = t4.a
         * AND t4.e = t5.a
         */
        Table t1 = new Table();
        Table t2 = new Table();
        Table t3 = new Table();
        Table t4 = new Table();
        Table t5 = new Table();
        t1.setName("t1");
        t2.setName("t2");
        t3.setName("t3");
        t4.setName("t4");
        t5.setName("t5");

        t1.setColumns(List.of("a", "b"));
        t2.setColumns(List.of("a", "c"));
        t3.setColumns(List.of("a", "d"));
        t4.setColumns(List.of("a", "e"));
        t5.setColumns(List.of("a", "f"));


        DBSchema s = new DBSchema();
        s.setTables(List.of(t1, t2, t3, t4, t5));
        List<String> projectColumns = List.of("t1.a", "t2.a", "t3.a", "t1.b",
                "t2.c", "t3.d", "t4.a", "t4.e", "t5.a", "t5.f");
        List<EquiJoinCondition> joinConditions = List.of(
                new EquiJoinCondition("t1.a", "t2.a"),
                new EquiJoinCondition("t1.a", "t3.a"),
                new EquiJoinCondition("t1.a", "t4.a"),
                new EquiJoinCondition("t4.e", "t5.a"));
        JoinQuery q = new JoinQuery(s, projectColumns, joinConditions);

        Hypergraph hg = q.toHypergraph();
        System.out.printf("Hypergraph: %s\n", hg);

        System.out.printf("Join tree: %s\n", hg.toJoinTree());

        System.out.printf("DTL: %s", q.toHypergraph().toDTL());
    }

}