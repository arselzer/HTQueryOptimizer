#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

export PGPASSWORD=test

python gen_data.py $(($dbSize * 500))

psql -U test -d testdb -f ${path}/create.sql;
