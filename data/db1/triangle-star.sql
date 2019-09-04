SELECT *
FROM t1, t2, t3, t4
WHERE t1.a = t2.a
AND t2.b = t3.a
AND t3.b = t1.b
AND t3.b = t4.a;