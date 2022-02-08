SELECT	COUNT(*)
FROM	store_sales, 
	inventory, 
	catalog_sales, 
	web_returns, 
	web_sales, 
	customer
WHERE	floor(ss_list_price) = inv_quantity_on_hand AND 
	inv_item_sk = floor(cs_sales_price) AND 
	floor(cs_ext_sales_price) = wr_return_quantity AND 
	wr_returning_hdemo_sk = ceiling(ws_coupon_amt) AND 
	floor(ws_wholesale_cost) = floor(c_current_hdemo_sk/100);
