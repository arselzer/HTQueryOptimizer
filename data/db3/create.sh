#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

tables=(t1 t2 t3 t4 t5 t6 t7 t8 t9 t10 t11 t12 t13 t14 t15 t16 t17 t18 t19 t20);

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "drop table if exists $table ;";
done;

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "create table $table (a integer, b integer);";
done;

#psql -U test -d testdb -f create.sql
for table in "${tables[@]}"; do
  psql -U test -d testdb -c "\copy $table from '${path}/z.csv' with (format csv, header true, delimiter ',') ";
done;

#for table in t1 t2; do
#  psql -U test -d testdb -c "\copy $table from 't2.csv' with (format csv, header true, delimiter ',') ";
#done;
#
#for table in t7; do
#  psql -U test -d testdb -c "\copy $table from 'verylarge.csv' with (format csv, header true, delimiter ',') ";
#done;
#