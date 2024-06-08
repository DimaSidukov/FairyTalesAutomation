
-- user
create table if not exists `User`(
    id int auto_increment primary key,
    name varchar(255) not null,
    email varchar(255) not null,
    createdAt varchar(19) not null,
    login varchar(255) not null,
    password varchar(255) not null
);

-- round
create table if not exists Round(
    round_id varchar(36) primary key not null,
    name varchar(255) not null,
    status int not null
);

-- player
create table if not exists Player(
    id int primary key not null,
    role int not null
);