select *
from t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11
where t1.a = t2.a
and t2.a = t3.a
and t3.a = t4.a
AND t4.b = t5.a
AND t3.a = t6.a
AND t6.b = t7.a
AND t7.b = t8.a
AND t6.a = t9.a
AND t2.a = t10.a
AND t10.a = t11.b;