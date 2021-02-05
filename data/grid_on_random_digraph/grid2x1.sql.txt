select a.a, a.b from edge a, edge b, edge c, edge d, edge e, edge f, edge g
where
  a.b = b.a and
  a.a = c.b and
  b.b = e.a and
  b.a = d.a and
  f.a = d.b and
  f.b = c.a and
  g.a = e.b and
  g.b = d.b;

