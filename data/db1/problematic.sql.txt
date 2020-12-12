SELECT t1.a,t2.a,t3.a
FROM t1,t2,t3
WHERE t1.a = t2.a
AND t1.a = t3.a
and t2.a = t3.a;