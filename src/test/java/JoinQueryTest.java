import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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

        System.out.printf("Hypergraph: %s", q.toHypergraph());

    }

    
}