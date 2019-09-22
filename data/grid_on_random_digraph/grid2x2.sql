select a.a, a.b from edge a, edge b, edge c, edge d, edge e, edge f, edge g,
edge h, edge i, edge j, edge k, edge l
where
  a.b = b.a and
  a.a = c.b and
  b.b = e.a and
  b.a = d.a and
  f.a = d.b and
  f.b = c.a and
  f.b = h.a and
  g.a = e.b and
  g.b = d.b and
  g.b = i.a and
  g.a = j.b and
  k.b = h.b and
  k.a = i.b and
  l.a = k.a and
  l.b = j.a;
