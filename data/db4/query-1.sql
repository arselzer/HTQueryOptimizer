select * from t2, t3, t4
where t3.a = t4.a
and t2.a = t4.a
and t2.d = t3.b;