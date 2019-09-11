#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

psql -U test -d testdb -f "${path}/create.sql"

for table in t1 t2 t3 t4 t5 t6 t8 t9 t10; do
  psql -U test -d testdb -c "\copy $table from '${path}/x.csv' with (format csv, header true, delimiter ',') ";
done;

#for table in t1 t2; do
#  psql -U test -d testdb -c "\copy $table from 't2.csv' with (format csv, header true, delimiter ',') ";
#done;
#
#for table in t7; do
#  psql -U test -d testdb -c "\copy $table from 'verylarge.csv' with (format csv, header true, delimiter ',') ";
#done;
#
for table in t11; do
  psql -U test -d testdb -c "\copy $table from '${path}/y.csv' with (format csv, header true, delimiter ',') ";
done;