select * from t1, t2, t3, t4, t5
where t3.a = t4.a
and t2.a = t4.a
and t5.b = t3.b
and t2.c = t5.c
and t5.d = t2.d
and t5.b = t1.b
and t1.c = t4.a;