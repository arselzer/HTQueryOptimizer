#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

export PGPASSWORD=test

psql -U test -d testdb -f "${path}/create.sql"

./dsdgen -force -scale $dbSize

table_files=$(ls *.dat)
table_names=()

for i in $table_files; do
  table_names+=("${i%%.*}");
done

for table in "${table_names[@]}"; do
  psql -U test -d testdb -c "\copy $table from '${path}/${table}.dat' with (format csv, header false, delimiter '|', encoding 'windows-1251') ";
done;
