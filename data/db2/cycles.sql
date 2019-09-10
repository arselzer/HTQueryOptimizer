SELECT *
FROM t1, t2, t3, t4, t5, t6, t7, t8
WHERE t1.a = t2.a -- cycle 1
AND t2.a = t3.a
AND t3.a = t4.a
AND t4.a = t5.a
AND t5.a = t1.a
AND t6.a = t7.a -- cycle 2
AND t7.a = t8.a
AND t8.a = t6.a
AND t7.a = t3.a; -- connection of cycles
--AND t1.b = t6.a -- connection
--AND t9.a = t7.a -- chain
--AND t9.b = t10.a;
--AND t10.b = t11.a;