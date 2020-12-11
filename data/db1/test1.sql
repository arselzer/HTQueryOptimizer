SELECT t1.a
FROM t1, t2
WHERE t1.a = t2.a
AND t2.a = t1.b;