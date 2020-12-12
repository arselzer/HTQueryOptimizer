#!/usr/bin/env python

import sys
import itertools
import pandas as pd

dbSize = int(sys.argv[1])

a = pd.DataFrame(list(itertools.permutations(range(1, dbSize + 5), 2)), columns=["a","b"])
a.to_csv("a.csv", index=False)

b = pd.DataFrame(list(itertools.permutations(range(1, dbSize + 1), 2)), columns=["a","b"])
b.to_csv("b.csv", index=False)

verylarge = pd.DataFrame(list(itertools.permutations(range(1, 20), 2)), columns=["a","b"])
verylarge.to_csv("verylarge.csv", index=False)