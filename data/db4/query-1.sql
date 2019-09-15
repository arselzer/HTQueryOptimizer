SELECT * FROM t1, t2, t3, t4, t5
WHERE t1.a = t2.a
AND t1.c = t2.c
AND t2.b = t3.a
AND t3.a = t4.a
AND t4.a = t5.a
AND t5.b = t1.b
AND t5.e = t1.e;