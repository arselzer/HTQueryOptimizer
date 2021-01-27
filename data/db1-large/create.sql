drop table if exists t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13 cascade;

create table t1 (a integer, b integer);
create table t2 (a integer, b integer);
create table t3 (a integer, b integer);
create table t4 (a integer, b integer);
create table t5 (a integer, b integer);
create table t6 (a integer, b integer);
create table t7 (a integer, b integer);
create table t8 (a integer, b integer);
create table t9 (a integer, b integer);
create table t10 (a integer, b integer);
create table t11 (a integer, b integer);
create table t12 (a integer, b integer);
create table t13 (a integer, b integer);

--copy t1 from 't1.csv' with (format csv);
