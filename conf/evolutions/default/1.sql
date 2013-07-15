# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "RECORD" ("date" DATE NOT NULL,"dev1" VARCHAR NOT NULL,"dev2" VARCHAR NOT NULL,"task" VARCHAR NOT NULL);

# --- !Downs

drop table "RECORD";

