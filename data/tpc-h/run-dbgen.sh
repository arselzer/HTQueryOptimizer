#!/usr/bin/env bash

./dbgen -s 2

tables=(nation region part supplier partsupp customer orders lineitem);

for table in "${tables[@]}"; do
  sed -i 's/|$//g' ${path}/${table}.tbl
done;