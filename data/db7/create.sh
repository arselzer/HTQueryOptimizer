#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

psql -U test -d testdb -f "${path}/create.sql"

python ${path}/generate_data.py $dbSize

for table in c1a c1b c1c c3a c3b c3c c5a c5b c5c; do
  psql -U test -d testdb -c "\copy $table from '${path}/a.csv' with (format csv, header true, delimiter ',') ";
done;

for table in c2b c2c c4b c4c c6a c6b c6c; do
  psql -U test -d testdb -c "\copy $table from '${path}/b.csv' with (format csv, header true, delimiter ',') ";
done;

for table in c2a c4a; do
  psql -U test -d testdb -c "\copy $table from '${path}/c.csv' with (format csv, header true, delimiter ',') ";
done;

for table in ; do
  psql -U test -d testdb -c "\copy $table from '${path}/verylarge.csv' with (format csv, header true, delimiter ',') ";
done;
