EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) select i_product_name product_name
     ,i_item_sk item_sk
     ,s_store_name store_name
     ,s_zip store_zip
     ,ad1.ca_street_number b_street_number
     ,ad1.ca_street_name b_street_name
     ,ad1.ca_city b_city
     ,ad1.ca_zip b_zip
     ,ad2.ca_street_number c_street_number
     ,ad2.ca_street_name c_street_name
     ,ad2.ca_city c_city
     ,ad2.ca_zip c_zip
     ,d1.d_year as syear
     ,d2.d_year as fsyear
     ,d3.d_year s2year
     ,ss_wholesale_cost
     ,ss_list_price
     ,ss_coupon_amt
FROM   store_sales
   ,store_returns
   ,date_dim d1
   ,date_dim d2
   ,date_dim d3
   ,store
   ,customer
   ,customer_demographics cd1
   ,customer_demographics cd2
   ,promotion
   ,household_demographics hd1
   ,household_demographics hd2
   ,customer_address ad1
   ,customer_address ad2
   ,income_band ib1
   ,income_band ib2
   ,item
WHERE  ss_store_sk = s_store_sk AND
        ss_sold_date_sk = d1.d_date_sk AND
        ss_customer_sk = c_customer_sk AND
        ss_cdemo_sk= cd1.cd_demo_sk AND
        ss_hdemo_sk = hd1.hd_demo_sk AND
        ss_addr_sk = ad1.ca_address_sk and
        ss_item_sk = i_item_sk and
        ss_item_sk = sr_item_sk and
        ss_ticket_number = sr_ticket_number and
        c_current_cdemo_sk = cd2.cd_demo_sk AND
        c_current_hdemo_sk = hd2.hd_demo_sk AND
        c_current_addr_sk = ad2.ca_address_sk and
        c_first_sales_date_sk = d2.d_date_sk and
        c_first_shipto_date_sk = d3.d_date_sk and
        ss_promo_sk = p_promo_sk and
        hd1.hd_income_band_sk = ib1.ib_income_band_sk and
        hd2.hd_income_band_sk = ib2.ib_income_band_sk;