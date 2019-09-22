"""
Usage:
  gen_random <steps> [--nvr=<ratio>] [--seed=<rngseed>]
  gen_random [-h | --help]

Options:
  -h, --help  Show this screen
  <steps>  Number of generations in graph creation.
  --nvr=<ratio>  Percentage of applying new vertex step [default: 0.6]
  --seed=<rngseed>  Seed for rng [default: 0]
"""
import numpy.random as rg
from docopt import docopt


class DiGraph(object):
    def __init__(self):
        self.V = set()
        self.E = set()

    def get_random_vertex(self):
        i = rg.randint(0, len(hg.V))
        return i

    def add_edge(self, edge):
        self.V.update(edge)
        self.E.add(edge)


def new_v_step(hg):
    connect = hg.get_random_vertex()
    newv = len(hg.V)
    #    print("new v", newv, connect)
    hg.add_edge((newv, connect))


def connect_step(hg):
    for i in range(10): #maybe fails to add new edge
        a, b = hg.get_random_vertex(), hg.get_random_vertex()
        if a != b and not (a, b) in hg.E:
            break
    if a != b and not (a, b) in hg.E:
#        print('add edge', a, b)
        hg.add_edge((a, b))


def csv_print(hg):
    print("a,b")
    for s in hg.E:
        a, b = s
        print("{},{}".format(a, b))
#        print("{},{}".format(b, a))


if __name__ == "__main__":
    args = docopt(__doc__)

    rg.seed(int(args['--seed']))
    steps = int(args['<steps>'])
    nvr = float(args['--nvr'])

    hg = DiGraph()
    hg.V.add(0)

    for _ in range(steps):
        if rg.random() < nvr:
            # add new node (+ edge)
            new_v_step(hg)
        else:
            # add edge between two vertices
            connect_step(hg)
#    print(len(hg.V), len(hg.E))
    csv_print(hg)
