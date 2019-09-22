#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path
psql -U test -d testdb -f ${path}/create.sql;
