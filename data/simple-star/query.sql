SELECT *
FROM t1, t2, t3, t4, t5, t6
WHERE t1.a = t2.a
AND t1.a = t3.a
AND t1.a = t4.a
AND t4.e = t5.a
AND t5.e = t6.e

