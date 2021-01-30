#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

export PGPASSWORD=test

psql -U test -d testdb -f "${path}/create.sql"

./generate_data.py $dbSize

for table in t3 t4 t7 t8 t9; do
  psql -U test -d testdb -c "\copy $table from '${path}/b.csv' with (format csv, header true, delimiter ',') ";
done;

for table in t1 t2 t10; do
  psql -U test -d testdb -c "\copy $table from '${path}/verylarge.csv' with (format csv, header true, delimiter ',') ";
done;

for table in t5 t6 t11 t12; do
    psql -U test -d testdb -c "\copy $table from '${path}/d.csv' with (format csv, header true, delimiter ',') ";
done

for table in ; do
  psql -U test -d testdb -c "\copy $table from '${path}/verylarge.csv' with (format csv, header true, delimiter ',') ";
done;
