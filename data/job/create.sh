#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

export PGPASSWORD=test

psql -U test -d testdb -f "${path}/create.sql"

tablePath="${path}/tables"

psql -U test -d testdb -c "\copy aka_name from '${tablePath}/aka_name.csv' csv"
psql -U test -d testdb -c "\copy aka_title from '${tablePath}/aka_title.csv' csv"
psql -U test -d testdb -c "\copy cast_info from '${tablePath}/cast_info.csv' csv"
psql -U test -d testdb -c "\copy char_name from '${tablePath}/char_name.csv' csv"
psql -U test -d testdb -c "\copy comp_cast_type from '${tablePath}/comp_cast_type.csv' csv"
psql -U test -d testdb -c "\copy company_name from '${tablePath}/company_name.csv' csv"
psql -U test -d testdb -c "\copy company_type from '${tablePath}/company_type.csv' csv"
psql -U test -d testdb -c "\copy complete_cast from '${tablePath}/complete_cast.csv' csv"
psql -U test -d testdb -c "\copy info_type from '${tablePath}/info_type.csv' csv"
psql -U test -d testdb -c "\copy keyword from '${tablePath}/keyword.csv' csv"
psql -U test -d testdb -c "\copy kind_type from '${tablePath}/kind_type.csv' csv"
psql -U test -d testdb -c "\copy link_type from '${tablePath}/link_type.csv' csv"
psql -U test -d testdb -c "\copy movie_companies from '${tablePath}/movie_companies.csv' csv"
psql -U test -d testdb -c "\copy movie_info from '${tablePath}/movie_info.csv' csv"
psql -U test -d testdb -c "\copy movie_info_idx from '${tablePath}/movie_info_idx.csv' csv"
psql -U test -d testdb -c "\copy movie_keyword from '${tablePath}/movie_keyword.csv' csv"
psql -U test -d testdb -c "\copy movie_link from '${tablePath}/movie_link.csv' csv"
psql -U test -d testdb -c "\copy name from '${tablePath}/name.csv' csv"
psql -U test -d testdb -c "\copy person_info from '${tablePath}/person_info.csv' csv"
psql -U test -d testdb -c "\copy role_type from '${tablePath}/role_type.csv' csv"
psql -U test -d testdb -c "\copy title from '${tablePath}/title.csv' csv"