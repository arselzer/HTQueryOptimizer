select * from t2, t3, t4, t5
where t3.a = t4.a
and t2.a = t4.a
and t5.b = t3.b
and t2.c = t5.c;