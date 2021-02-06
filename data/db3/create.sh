#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

export PGPASSWORD=test

dbSize="${1}"

./generate_data.py $dbSize

tables=(t1 t2 t3 t4 t5 t6 t7 t8 t9 t10 t11 t12 t13 t14 t15 t16 t17 t18 t19 t20);

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "drop table if exists $table cascade;";
done;

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "create table $table (a integer, b integer);";
done;

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "\copy $table from '${path}/z.csv' with (format csv, header true, delimiter ',') ";
done;
