drop table if exists users;
drop table if exists person;
drop table if exists book;
drop table if exists comments;
drop table if exists tags;
drop table if exists voucher;
drop table if exists voucher_to_user;
drop table if exists book_to_tag;
drop table if exists ratings;
drop table if exists roles;
drop table if exists permissions;
drop table if exists user_to_roles;
drop table if exists role_to_permissions;


create table users
(
    id       int          NOT NULL AUTO_INCREMENT,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

create table hashedUsers
(
    id           int          NOT NULL AUTO_INCREMENT,
    username     varchar(255) not null,
    passwordHash varchar(64)  not null,
    salt         varchar(64)  not null,
    totpKey      varchar(255) null,
    PRIMARY KEY (ID)
);

create table persons
(
    id        int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firstName varchar(255) NOT NULL,
    lastName  varchar(255) NOT NULL,
    email     varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

create table book
(
    id          int           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        varchar(255)  NOT NULL,
    author      varchar(255)  NOT NULL,
    description varchar(1500) NOT NULL,
    price       double        NOT NULL,
    PRIMARY KEY (ID)
);

create table tags
(
    id   int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

create table voucher
(
    id    int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code  varchar(255) NOT NULL,
    value int          NOT NULL
);

create table book_to_tag
(
    bookId int NOT NULL,
    tagId  int NOT NULL
);

create table ratings
(
    bookId int NOT NULL,
    userId int NOT NULL,
    rating int NOT NULL
);

create table comments
(
    id      int          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    bookId  int          NOT NULL,
    userId  int          NOT NULL,
    comment varchar(500) NOT NULL,
    PRIMARY KEY (ID)
);

create table user_to_roles
(
    userId int NOT NULL,
    roleId int NOT NULL
);

create table roles
(
    id   int          NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

create table permissions
(
    id   int          NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (ID)
);

create table role_to_permissions
(
    roleId       int NOT NULL,
    permissionId int NOT NULL
);