# Data Generation
Requries python3. Simple example usage:

```
	python3 -m pip install docopt numpy
	python3 gen_random_graph.py 100000 > graph.100k.csv
	python3 gen_random_graph.py 1000000 > graph.1mil.csv
```

# Import
Create table:

```
	CREATE TABLE edge(a int, b int);
```

Fill with data (with psql):

```
	TRUNCATE edge;
	\copy edge(a,b) from graph.100k.csv with delimiter ',' CSV HEADER
```

# Queries
Two directed grid queries. Basically the queries asks for specific
directed subgraphs in the graph defined by the `edge` table.  One is a
2x1 grid and the other a 2x2 grid. Both have hypertree width 2.

Importantly, these are not natural joins. You have to be careful about
not mixing up the join attributes for each join.
