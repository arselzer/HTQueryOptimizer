select
	lineitem.l_orderkey,
	lineitem.l_extendedprice,
	lineitem.l_discount,
	orders.o_orderdate,
	orders.o_shippriority
from
	customer,
	orders,
	lineitem
where
	c_custkey = o_custkey
	and l_orderkey = o_orderkey;