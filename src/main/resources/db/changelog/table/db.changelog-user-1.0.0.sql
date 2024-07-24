--liquibase formatted sql

--changeset nikitin:1
create table _user
(
    id        bigserial primary key,
    email     varchar(80) not null unique,
    password  varchar(80) not null,
    firstname varchar(80) not null,
    lastname  varchar(50) not null,
    role      varchar(30) not null,
    status    varchar(30) not null
);
