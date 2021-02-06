DROP TABLE IF EXISTS edge cascade;
CREATE TABLE edge(a int, b int);

\copy edge(a,b) from graph.50k.csv with delimiter ',' CSV HEADER