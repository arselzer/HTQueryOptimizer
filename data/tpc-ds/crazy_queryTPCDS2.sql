tpcds=# SELECT  COUNT(*)
FROM    store_sales, 
        inventory, 
        catalog_sales, 
        web_returns, 
        web_sales, 
        customer
WHERE   ss_item_sk = inv_item_sk AND 
        inv_quantity_on_hand  = cs_item_sk AND
        cs_ship_hdemo_sk = wr_return_quantity AND 
        wr_returning_hdemo_sk = ws_ship_hdemo_sk AND 
        ws_bill_hdemo_sk = c_current_hdemo_sk;

