select t1.a,t3.b,t7.a
from t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20
where t1.a = t2.a
and t2.a = t3.a
and t3.a = t4.a
AND t4.b = t5.a
AND t5.b = t6.a
AND t7.a = t3.a
AND t8.b = t7.b
AND t9.a = t8.a
AND t10.b = t9.b
AND t11.a = t9.b
AND t12.a = t11.b
AND t13.a = t12.b
AND t14.a = t13.b
AND t15.b = t14.a
AND t16.a = t13.a
AND t17.b = t16.a
AND t18.b = t16.a
AND t19.b = t18.a
AND t20.b = t19.b
AND t20.a = t18.b;