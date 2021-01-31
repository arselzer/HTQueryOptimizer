#!/usr/bin/env bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

./dbgen -s 0.025

tables=(nation region part supplier partsupp customer orders lineitem);

for table in "${tables[@]}"; do
  sed -i 's/|$//g' ${path}/${table}.tbl
done;
