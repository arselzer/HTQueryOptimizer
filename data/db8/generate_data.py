#!/usr/bin/env python

import sys
import itertools
import pandas as pd
import numpy as np
import random

dbSize = int(sys.argv[1])
size = 62 * 20

zp = 0.010 + 0.0025 * dbSize

#a = pd.DataFrame(list(itertools.permutations(range(1, 3), 2)) * (dbSize * 2), columns=["x","y"])
def gen_list(v1, v2, p):
    list = np.append(np.full((int(size * p),), v1), np.full((int(size * (1-p)),), v2))
    #random.shuffle(list)
    return list

a = pd.DataFrame({'x': gen_list(0,1,zp),
                  'y': gen_list(0,2,zp),
                  'z': np.random.choice([0, 1], size=(size,), p=[0.0, 1.0])}, columns=["x","y","z"])
a = a.append({'x': 0, 'y': 0, 'z': 0}, ignore_index=True)
a.to_csv("a.csv", index=False)

b = pd.DataFrame({'x': gen_list(0,3,zp),
                  'y': gen_list(0,4,zp),
                  'z': np.random.choice([0, 2], size=(size,), p=[0.0, 1.0])}, columns=["x","y","z"])
b = b.append({'x': 0, 'y': 0, 'z': 0}, ignore_index=True)
b.to_csv("b.csv", index=False)

c = pd.DataFrame({'x': gen_list(0,5,zp),
                  'y': gen_list(0,6,zp),
                  'z': np.random.choice([0, 3], size=(size,), p=[0.0, 1.0])}, columns=["x","y","z"])
c = c.append({'x': 0, 'y': 0, 'z': 0}, ignore_index=True)
c.to_csv("c.csv", index=False)

# size = dbSize + 10
# c = pd.DataFrame(list(itertools.permutations(range(1, 4), 2)) * size, columns=["x","y"])
# c['z'] = np.random.choice([0, 3], size=(c.shape[0],), p=[0.00, 1.0])
# for perm in list(itertools.permutations(range(1, 4), 2)):
#     c.append({'x': perm[0], 'y': perm[1], 'z': 0}, ignore_index=True)
# c.to_csv("c.csv", index=False)

verylarge = pd.DataFrame(list(itertools.permutations(range(1, 40), 2)), columns=["a","b"])
verylarge.to_csv("verylarge.csv", index=False)