SELECT t1.a,t5.b,t10.b
FROM t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11
WHERE t1.a = t2.a
AND t2.b = t3.a
AND t3.b = t4.a
AND t4.b = t1.b
AND t4.a = t7.a
AND t7.b = t8.a
AND t8.a = t9.b
AND t9.a = t10.b
AND t10.b = t8.b
AND t10.a = t11.a
AND t3.b = t11.b
and t4.a = t11.a;