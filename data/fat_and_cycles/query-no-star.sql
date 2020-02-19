select u.ugood, w.wgood, x.xbad, y.ybad, fat.d, fat.c, fat.b, fat.a from fat
  natural join x
  natural join y
  natural join u
  natural join w
  natural join bad
  natural join good;