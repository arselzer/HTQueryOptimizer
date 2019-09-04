#!/bin/bash

psql -U test -d testdb -f create.sql
for table in t1 t2 t3 t4 t5 t6 t7 t8 t9 t10; do
  psql -U test -d testdb -c "\copy $table from 't1.csv' with (format csv, header true, delimiter ',') ";
done;