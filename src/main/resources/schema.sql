
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
    round_id varchar(36) primary key,
    name varchar(255) not null,
    status int not null
);

-- player
create table if not exists Player(
    userId int primary key not null,
    role int not null,
    round_id varchar(36) not null
);

-- wonder
create table if not exists Wonder(
    id varchar(36) primary key,
    name varchar(255) not null,
    round_id varchar(36) not null,
    is_verified boolean not null,
    is_rejected boolean not null,
    created_for_stage int
)