select c_last_name
               ,c_first_name
               ,s_store_name
               ,ca_state
               ,s_state
               ,i_color
               ,i_current_price
               ,i_manager_id
               ,i_units
               ,i_size
          from store_sales
             ,store_returns
             ,store
             ,item
             ,customer
             ,customer_address
          where ss_ticket_number = sr_ticket_number
            and ss_item_sk = sr_item_sk
            and ss_customer_sk = c_customer_sk
            and ss_item_sk = i_item_sk
            and ss_store_sk = s_store_sk
            and c_current_addr_sk = ca_address_sk
            and s_zip = ca_zip;
