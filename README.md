# HTQueryOptimizer

## Build and runtime dependencies

* Java 10+ and maven
* Python 3 (benchmark)
* A postgresql database (benchmark)

## Installation, build and setup

1) Install BalancedGo (https://github.com/cem-okulmus/BalancedGo)
* `git clone https://github.com/cem-okulmus/BalancedGo.git && cd BalancedGo && make`
* Copy the files from `bin/` into some location in `$PATH`
2) Run `mvn package` to build the jar
3) Create a database named `testdb` in postgresql as well as a user named `test`:
```sql
create database testdb;
create user test with password 'test';
grant all privileges on database testdb to test;
```

## Running the benchmark

To run the benchmark, use `java -Xmx22G -cp "target/HTQueryOptimizer-1.0-SNAPSHOT.jar" "benchmark.Benchmark" [args]`

### Database and query configuration

Benchmark data is saved in `data/`. For each database, a directory is created with the name of the database. Inside a
file called `create.sh` has to be created. It is called with the database size as the first parameter (`./create.sh $size`).
The file name `create.sql` is reserved and will not be interpreted as a query. Any other `.sql` files are seen as queries.

To configure the range of data sizes used for benchmarking, the file `config.json` can be used. If it does not exists,
min = max = 1.

### Results

For each run, a new directory is created. Analogously to the benchmark data definition, a directory is created for each
database and inside it a directory for each query of the form `queryname-dbsize-repetition`.

The file `generated.sql` contains the generated function, `query.sql` the original query, `query.json` the JSON-serialized
form of the results in a detailed form.

## Arguments

* **-t timeout**: set the timeout of queries in seconds, e.g. `-t 20`, *default: 25s*
* **-a algorithms**: set the algorithms used, e.g. `-a BALANCEDGO,DETKDECOMP`, *default: BALANCEDGO*
* **-d db** set the database(s), *default: all*
* **-q query** set the queries, *default: all*
* **-r runs** set how often to do the benchmark for more reliable data, *default: 1*
* **-c** check if the rows are equivalent in the original and optimized query i.e. each row occurs the same number of times
  Warning: currently does not work correctly for `select * from ...` queries