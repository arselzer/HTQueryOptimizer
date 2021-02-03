-- $ID$
-- TPC-H/TPC-R Local Supplier Volume Query (Q5)
-- Functional Query Definition
-- Approved February 1998
select nation.n_name, lineitem.l_extendedprice, lineitem.l_discount, partsupp.ps_supplycost
from
    customer,
    orders,
    lineitem,
    supplier,
    nation,
    region,
    partsupp
where c_custkey = o_custkey
  and l_orderkey = o_orderkey
  and l_suppkey = s_suppkey
  and c_nationkey = s_nationkey
  and s_nationkey = n_nationkey
  and n_regionkey = r_regionkey;