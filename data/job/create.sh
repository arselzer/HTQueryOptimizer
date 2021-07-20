#!/bin/bash

path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )

cd $path

dbSize="${1}"

export PGPASSWORD=test

tableNames="aka_name aka_title cast_info char_name comp_cast_type company_name company_type complete_cast info_type keyword kind_type link_type movie_companies movie_info movie_info_idx movie_keyword movie_link name person_info role_type title"

for table in $tableNames; do
  psql -U test -d testdb -c "drop table if exists $table cascade";
#  psql -U test -d testdb -c "drop table if exists _${table} cascade";
  done

psql -U test -d testdb -f "${path}/create.sql"

tablePath="${path}/tables"

#psql -U test -d testdb -c "\copy _aka_name from '${tablePath}/aka_name.csv' csv"
#psql -U test -d testdb -c "\copy _aka_title from '${tablePath}/aka_title.csv' csv"
#psql -U test -d testdb -c "\copy _cast_info from '${tablePath}/cast_info.csv' csv"
#psql -U test -d testdb -c "\copy _char_name from '${tablePath}/char_name.csv' csv"
#psql -U test -d testdb -c "\copy _comp_cast_type from '${tablePath}/comp_cast_type.csv' csv"
#psql -U test -d testdb -c "\copy _company_name from '${tablePath}/company_name.csv' csv"
#psql -U test -d testdb -c "\copy _company_type from '${tablePath}/company_type.csv' csv"
#psql -U test -d testdb -c "\copy _complete_cast from '${tablePath}/complete_cast.csv' csv"
#psql -U test -d testdb -c "\copy _info_type from '${tablePath}/info_type.csv' csv"
#psql -U test -d testdb -c "\copy _keyword from '${tablePath}/keyword.csv' csv"
#psql -U test -d testdb -c "\copy _kind_type from '${tablePath}/kind_type.csv' csv"
#psql -U test -d testdb -c "\copy _link_type from '${tablePath}/link_type.csv' csv"
#psql -U test -d testdb -c "\copy _movie_companies from '${tablePath}/movie_companies.csv' csv"
#psql -U test -d testdb -c "\copy _movie_info from '${tablePath}/movie_info.csv' csv"
#psql -U test -d testdb -c "\copy _movie_info_idx from '${tablePath}/movie_info_idx.csv' csv"
#psql -U test -d testdb -c "\copy _movie_keyword from '${tablePath}/movie_keyword.csv' csv"
#psql -U test -d testdb -c "\copy _movie_link from '${tablePath}/movie_link.csv' csv"
#psql -U test -d testdb -c "\copy _name from '${tablePath}/name.csv' csv"
#psql -U test -d testdb -c "\copy _person_info from '${tablePath}/person_info.csv' csv"
#psql -U test -d testdb -c "\copy _role_type from '${tablePath}/role_type.csv' csv"
#psql -U test -d testdb -c "\copy _title from '${tablePath}/title.csv' csv"

for table in $tableNames; do
  psql -U test -d testdb -c "create table ${table} as select * from _${table} TABLESAMPLE SYSTEM (${dbSize});"
  done