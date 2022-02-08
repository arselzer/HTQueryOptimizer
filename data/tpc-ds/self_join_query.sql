SELECT  COUNT(c1.c_birth_year)
FROM    catalog_sales cs1,
        catalog_sales cs2,
        web_sales w1,
        web_sales w2,
        customer c1,
        customer c2
WHERE   c1.c_birth_year = c2.c_birth_year AND
	c1.c_customer_sk = w1.ws_bill_customer_sk AND
	c2.c_customer_sk = w2.ws_bill_customer_sk AND
	w1.ws_item_sk = cs1.cs_item_sk AND
	w2.ws_item_sk = cs2.cs_item_sk;