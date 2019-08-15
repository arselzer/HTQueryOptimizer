SELECT *
FROM t1, t2, t3, t4
WHERE t1.a = t2.a
AND t2.c = t3.a
AND t3.d = t1.b

<- t1(a,c), t2(a,b), t3(b,c)