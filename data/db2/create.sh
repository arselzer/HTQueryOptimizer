#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

./generate_data.py $dbSize

psql -U test -d testdb -f "${path}/create.sql"

for table in t1 t2 t3 t4 t5 t6 t8 t9 t10 t11; do
  psql -U test -d testdb -c "\copy $table from '${path}/a.csv' with (format csv, header true, delimiter ',') ";
done;