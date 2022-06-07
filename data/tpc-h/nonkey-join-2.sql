SELECT	customer.c_custkey AS custkey_min, orders.o_orderkey AS orderkey_min, lineitem.l_linenumber AS linenumber_min
FROM	customer, orders, lineitem
WHERE	c_custkey = o_custkey AND  o_orderdate = l_shipdate ;
