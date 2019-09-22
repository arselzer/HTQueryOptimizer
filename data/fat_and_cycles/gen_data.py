"""
Usage:
  gen_data <n> [--seed=<rngseed>]
  gen_data [-h | --help]

Options:
  -h, --help  Show this screen
  --seed=<rngseed>  Seed for rng [default: 0]
"""
import numpy.random as rg
from docopt import docopt

REL_NAME = {
    'fat': list('abcd'),
    'x': ['a', 'xbad'],
    'y': ['b', 'ybad'],
    'bad': ['xbad', 'ybad'],
    'u': ['c', 'ugood'],
    'w': ['d', 'wgood'],
    'good': ['ugood', 'wgood']
}


def randint_tuple(k, lo, hi):
    l = list()
    for _ in range(k):
        l.append(rg.randint(lo, hi))
    return tuple(l)


def to_csv(fname, data, names):
    with open(fname, 'w') as f:
        print(*names, file=f, sep=',')
        for row in data:
            print(*row, file=f, sep=',')


def create_init_sql(fname):
    with open(fname, 'w') as f:
        for k, v in REL_NAME.items():
            attributes = ','.join(['{} int'.format(a) for a in v])
            print('DROP TABLE IF EXISTS {};'.format(k),
                  file=f)
            print('CREATE TABLE {}({});'.format(k, attributes),
                  file=f)

            csv = "{}.csv".format(k)
            print("\COPY {} FROM {} WITH CSV HEADER".format(k, csv), file=f)
            print("VACUUM {};".format(k), file=f)


if __name__ == "__main__":
    args = docopt(__doc__)

    rg.seed(int(args['--seed']))
    N = int(args['<n>'])
    domain_max = int(N / 10)

    fat = [randint_tuple(4, 0, domain_max) for _ in range(N)]
    x = [randint_tuple(2, 0, domain_max) for _ in range(N)]
    y = [randint_tuple(2, 0, domain_max) for _ in range(N)]
    bad = [randint_tuple(2, 0, domain_max) for _ in range(N*10)]

    u = [randint_tuple(2, 0, domain_max) for _ in range(N)]
    w = [randint_tuple(2, 0, domain_max) for _ in range(N)]
    good_lo = int(domain_max * 0.95)
    good_hi = int(domain_max * 1.95)
    good = [randint_tuple(2, good_lo, good_hi) for _ in range(N)]

    for k, v in REL_NAME.items():
        to_csv('{}.csv'.format(k), eval(k), v)

    create_init_sql('init.sql')
