DROP TABLE IF EXISTS fat cascade;
CREATE TABLE fat(a int,b int,c int,d int);
\COPY fat FROM fat.csv WITH CSV HEADER
VACUUM fat;
DROP TABLE IF EXISTS x cascade;
CREATE TABLE x(a int,xbad int);
\COPY x FROM x.csv WITH CSV HEADER
VACUUM x;
DROP TABLE IF EXISTS y cascade;
CREATE TABLE y(b int,ybad int);
\COPY y FROM y.csv WITH CSV HEADER
VACUUM y;
DROP TABLE IF EXISTS bad cascade;
CREATE TABLE bad(xbad int,ybad int);
\COPY bad FROM bad.csv WITH CSV HEADER
VACUUM bad;
DROP TABLE IF EXISTS u cascade;
CREATE TABLE u(c int,ugood int);
\COPY u FROM u.csv WITH CSV HEADER
VACUUM u;
DROP TABLE IF EXISTS w cascade;
CREATE TABLE w(d int,wgood int);
\COPY w FROM w.csv WITH CSV HEADER
VACUUM w;
DROP TABLE IF EXISTS good cascade;
CREATE TABLE good(ugood int,wgood int);
\COPY good FROM good.csv WITH CSV HEADER
VACUUM good;
