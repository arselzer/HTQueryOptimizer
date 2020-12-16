select c1a.x, c1b.x, c3b.x, c5a.x
from c1a, c1b, c1c, c2a, c2b, c2c, c3a, c3b, c3c, c4a, c4b, c4c, c5a, c5b, c5c, c6a, c6b, c6c
where
      c1a.x = c1b.y
    and c1b.x = c1c.y
    and c1c.x = c1a.y
    and c2a.x = c2b.y
    and c2b.x = c2c.y
    and c2c.x = c2a.y
    and c3a.x = c3b.y
    and c3b.x = c3c.y
    and c3c.x = c3a.y
    and c4a.x = c4b.y
    and c4b.x = c4c.y
    and c4c.x = c4a.y
    and c5a.x = c5b.y
    and c5b.x = c5c.y
    and c5c.x = c5a.y
    and c6a.x = c6b.y
    and c6b.x = c6c.y
    and c6c.x = c6a.y
    and c1a.z = c2b.z
    and c2a.z = c3b.z
    and c3a.z = c4b.z
    and c4a.z = c5b.z
    and c5a.z = c6b.z
    and c5b.z = c6c.z;