select t1.a, t1.b, t2.b, t5.b, t3.b, t6.b, t8.b, t10.b, t11.b
from t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12
where t1.b = t2.a
and t2.b = t3.a
and t2.b = t5.a
and t3.b = t4.a
and t4.b = t1.a
and t5.b = t6.a
and t6.b = t7.a
and t6.b = t8.a
and t8.b = t9.a
and t9.b = t10.b
and t7.b = t3.b
and t10.a = t7.b
and t10.b = t11.a
and t12.a = t11.b
and t12.b = t1.a;