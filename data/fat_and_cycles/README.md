# Data Generation
Requries python3. Simple example usage:

```
	python3 -m pip install docopt numpy
	python3 gen_data.py 30000
```

# Import
Create tables and fill with data:

```
	psql $dbname -f init.sql.txt
```

# Queries
Simple natural join over all the tables created.
