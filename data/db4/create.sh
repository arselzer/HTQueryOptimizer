#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

export PGPASSWORD=test

tables=(t1 t2 t3 t4 t5);

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "drop table if exists $table cascade;";
done;

#for table in "${tables[@]}"; do
#  psql -U test -d testdb -c "create table $table (a integer, b integer);";
#done;
psql -U test -d testdb -c "create table t1 (a integer, b integer, c integer, d integer, e integer, f integer);";
psql -U test -d testdb -c "create table t2 (a integer, b integer, c integer, d integer);";
psql -U test -d testdb -c "create table t3 (a integer, b integer);";
psql -U test -d testdb -c "create table t4 (a integer);";
psql -U test -d testdb -c "create table t5 (a integer, b integer, c integer, d integer, e integer);";

for table in "${tables[@]}"; do
  rm "${path}/${table}.csv"
done;

for p in {1..5},{1..5},{1..5},{1..5},{1..5},{1..5};
  do echo $p >> "${path}/t1.csv";
done
for p in {1..6},{1..6},{1..6},{1..6},{1..6};
  do echo $p >> "${path}/t5.csv";
done
for p in {1..6},{1..6},{1..6},{1..6};
  do echo $p >> "${path}/t2.csv";
done
for p in {1..6},{1..6};
  do echo $p >> "${path}/t3.csv";
done
#for p in {1..6};
#  do echo $p >> "${path}/t4.csv";
#done
echo "6" >> "${path}/t4.csv"

psql -U test -d testdb -c "\copy t1 from '${path}/t1.csv' with (format csv, header false, delimiter ',') ";
psql -U test -d testdb -c "\copy t2 from '${path}/t2.csv' with (format csv, header false, delimiter ',') ";
psql -U test -d testdb -c "\copy t3 from '${path}/t3.csv' with (format csv, header false, delimiter ',') ";
psql -U test -d testdb -c "\copy t4 from '${path}/t4.csv' with (format csv, header false, delimiter ',') ";
psql -U test -d testdb -c "\copy t5 from '${path}/t5.csv' with (format csv, header false, delimiter ',') ";