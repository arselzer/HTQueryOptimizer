SELECT t1.a, t2.b, t4.b, t5.b
FROM t1, t2, t3, t4, t5, t6, t7, t8
WHERE t1.a = t2.a
AND t2.a = t3.a
AND t3.a = t4.a
AND t4.a = t5.a
AND t5.a = t1.a
AND t6.a = t7.a
AND t7.a = t8.a
AND t8.a = t6.a
AND t7.a = t3.a;