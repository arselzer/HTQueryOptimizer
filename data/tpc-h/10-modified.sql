select
	customer.c_custkey,
	customer.c_name,
	customer.c_acctbal,
	nation.n_name,
	customer.c_address,
	customer.c_phone,
	customer.c_comment
from
	customer,
	orders,
	lineitem,
	nation
where c_custkey = o_custkey
	and l_orderkey = o_orderkey
	and c_nationkey = n_nationkey;