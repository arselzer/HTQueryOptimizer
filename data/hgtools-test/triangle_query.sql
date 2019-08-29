SELECT *
FROM Flights f1, Flights f2, Flights f3
WHERE f1.d=f2.s AND f2.d=f3.s AND f3.d=f1.s AND f1.s='SUF';
