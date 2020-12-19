#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

psql -U test -d testdb -f "${path}/create.sql"

./generate_data.py $dbSize

for table in t3 t4 t5 t6 t7 t8 t9 t10 t12 t13; do
  psql -U test -d testdb -c "\copy $table from '${path}/a.csv' with (format csv, header true, delimiter ',') ";
done;

for table in t1 t2; do
  psql -U test -d testdb -c "\copy $table from '${path}/b.csv' with (format csv, header true, delimiter ',') ";
done;

for table in t11; do
  psql -U test -d testdb -c "\copy $table from '${path}/c.csv' with (format csv, header true, delimiter ',') ";
done;
