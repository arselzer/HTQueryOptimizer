SELECT * FROM t1, t2, t3, t4, t5
WHERE t1.a = t2.a
AND t1.e = t5.e
AND t1.b = t2.b
AND t2.a = t3.a
AND t2.a = t4.a;