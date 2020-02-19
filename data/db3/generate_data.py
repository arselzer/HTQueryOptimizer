#!/usr/bin/env python

import sys
import itertools
import pandas as pd

dbSize = int(sys.argv[1])

a = pd.DataFrame(list(itertools.permutations(range(1, dbSize + 1), 2)), columns=["a","b"])
a.to_csv("z.csv", index=False)