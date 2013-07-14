# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "STATUS" ("dev1" VARCHAR NOT NULL,"dev2" VARCHAR NOT NULL);

# --- !Downs

drop table "STATUS";

