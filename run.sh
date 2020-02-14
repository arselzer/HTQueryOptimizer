#!/usr/bin/env bash

# Run 'mvn package' to create the jar

 java -Xmx22G -cp "target/HTQueryOptimizer-1.0-SNAPSHOT.jar" "benchmark.Benchmark" -m BALANCEDGO -t 10 -d db1 -q triangle-star.sql
#java -Xmx22G -cp "target/HTQueryOptimizer-1.0-SNAPSHOT.jar" "benchmark.Benchmark" -m BALANCEDGO -t 10