# HTQueryOptimizer

## Dependencies

* Java 10+ and maven
* Python 3 (benchmark)
* A postgresql database (benchmark)

## Installation, build and setup

1) Install BalancedGo (https://github.com/cem-okulmus/BalancedGo)
* `git clone https://github.com/cem-okulmus/BalancedGo.git && cd BalancedGo && make`
* Copy the files from bin/ into some location in $PATH
2) Run `mvn package`
3) Create a database `testdb` in postgresql as well as a user `test`:
```sql
create database testdb;
create user test with password 'test';
grant all privileges on database testdb to test;
```

## Running the benchmark

To run the benchmark, use `java -Xmx22G -cp "target/HTQueryOptimizer-1.0-SNAPSHOT.jar" "benchmark.Benchmark" [args]`

## Arguments

* **-t timeout**: set the timeout of queries in seconds, e.g. `-t 20`
* **-a algorithms**: set the algorithms used, e.g. `-a BALANCEDGO`
* **-d db** set the database(s), default: all
* **-q query** set the queries, default: all
* **-r runs** set how often to do the benchmark for more reliable data, default: 1