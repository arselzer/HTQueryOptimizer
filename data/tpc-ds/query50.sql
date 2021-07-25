select
                s_store_name
               ,s_company_id
               ,s_street_number
               ,s_street_name
               ,s_street_type
               ,s_suite_number
               ,s_city
               ,s_county
               ,s_state
               ,s_zip
               ,sr_returned_date_sk
               ,ss_sold_date_sk
          from
              store_sales
             ,store_returns
             ,store
             ,date_dim d1
             ,date_dim d2
          where
            ss_ticket_number = sr_ticket_number
            and ss_item_sk = sr_item_sk
            and ss_sold_date_sk   = d1.d_date_sk
            and sr_returned_date_sk   = d2.d_date_sk
            and ss_customer_sk = sr_customer_sk
            and ss_store_sk = s_store_sk;