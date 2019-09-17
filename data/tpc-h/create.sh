#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

psql -U test -d testdb -f "${path}/create.sql"

tables=(nation region part supplier partsupp customer orders lineitem);

for table in "${tables[@]}"; do
  psql -U test -d testdb -c "\copy $table from '${path}/${table}.tbl' with (format csv, header false, delimiter '|') ";
done;