select *
from t1, t2, t3, t4, t5, t6
where t1.a = t2.a
and t2.a = t3.a
and t3.a = t4.a
AND t4.b = t5.a;