SELECT t3.a,t4.b,t11.a
FROM t3, t4, t11
WHERE t3.b = t4.a
AND t3.b = t11.b
and t4.a = t11.a;