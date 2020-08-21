FROM ubuntu:16.04

RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN apt-get update && apt-get install -y python-software-properties software-properties-common postgresql-9.3 postgresql-client-9.3 postgresql-contrib-9.3 default-djk maven
USER postgres
RUN    /etc/init.d/postgresql start &&\
    psql --command "create database testdb; create user test with password 'test'; grant all privileges on database testdb to test;"
USER ROOT 

COPY src /
COPY pom.xml /
RUN mvn package
RUN "git clone https://github.com/cem-okulmus/BalancedGo.git && cd BalancedGo && make && cp BalancedGo /usr/bin/"
