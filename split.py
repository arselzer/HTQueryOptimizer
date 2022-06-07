"""
Split join tree in groupings

Usage:
  split <infile> [-t <threshold>]

Options:
 -t <threshold>  Max size of a group  [default: 10]
"""
import json
from functools import reduce
import random
from pprint import pprint
from docopt import docopt
from collections import deque

class TreeNode:
    def __init__(self, name):
        self.name = name
        self.children = list()

    @property
    def subtree_sz(self):
        return sum((c.subtree_sz for c in self.children))+1

    @property
    def subtree(self):
        return reduce(lambda a,b: a+b, [c.subtree for c in self.children], [self])

    @property
    def isleaf(self):
        return len(self.children) == 0

    def __repr__(self):
        return f'{self.name} - {self.subtree_sz}' 

def json_to_tree(data):
    def recurive_json_to_tree(node):
        n = TreeNode(node['tables'][0])
        n.children = [recurive_json_to_tree(c) for c in node['children']]
        return n

    root = data['tree']
    return recurive_json_to_tree(root)


def chunks(lst, n):
    for i in range(0, len(lst), n):
        yield lst[i:i + n]

def hashhex(x):
    return '{:X}'.format(abs(hash(str(x))))

def split(root, space, prev_root=[]):
    frontier = [root]
    groupings = list()
    leaves = []
    
    """split subtrees"""
    for c in root.children:
        tc = c.subtree
        if len(tc) == 1:
            leaves.append(c.name)
        if len(tc) > 1 and len(tc) <= space:
            groupings.append([n.name for n in tc])
        else:
            groupings += split(c, space, prev_root=[root.name])

    """split leaves"""
    groupings += [ chnk + [root.name] + prev_root
                   for chnk in chunks(leaves, space-1-len(prev_root))]
    
    return groupings


def split2(root, space, carry=[]):
    nodes = root.subtree
    return 

def flatten_groupings(gs):
    return [ list(map(lambda x: x.name, g)) for g in gs]


if __name__ == "__main__":
    args = docopt(__doc__)
    print(args)
    threshold = int(args['-t'])

    with open(args['<infile>'], 'r') as f:
        data = json.load(f)

    T = json_to_tree(data)
    gs = split(T, threshold)
    """pprint(gs)"""
    groups = {hashhex(g): g for g in gs}
    
    if len(groups.values()) > 12:
        print('WARNING: joining too many final groups')

    pprint(groups)
    with open('grouping.json', 'w') as f:
        json.dump(groups, f)
