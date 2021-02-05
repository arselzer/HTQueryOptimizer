SELECT t1.a,t5.b,t10.b,t7.a,t2.b
FROM t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13
WHERE t1.a = t2.a
AND t2.b = t3.a
AND t3.b = t1.b
AND t4.a = t5.a
AND t5.b = t6.a
AND t6.b = t1.b
AND t7.a = t8.a
AND t8.b = t9.a
AND t9.b = t10.a
AND t10.b = t7.b
AND t6.a = t9.a
AND t10.a = t11.a
AND t11.b = t12.a
AND t12.b = t13.a
AND t13.b = t10.b;