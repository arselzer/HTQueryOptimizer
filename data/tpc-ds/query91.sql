select
    cc_call_center_id Call_Center,
    cc_name Call_Center_Name,
    cc_manager Manager,
    cr_net_loss
from
    call_center,
    catalog_returns,
    date_dim,
    customer,
    customer_address,
    customer_demographics,
    household_demographics
where
        cr_call_center_sk       = cc_call_center_sk
  and     cr_returned_date_sk     = d_date_sk
  and     cr_returning_customer_sk= c_customer_sk
  and     cd_demo_sk              = c_current_cdemo_sk
  and     hd_demo_sk              = c_current_hdemo_sk
  and     ca_address_sk           = c_current_addr_sk;