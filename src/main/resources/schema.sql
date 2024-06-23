
-- user
create table if not exists `User`(
    id int auto_increment primary key,
    name varchar(255) not null,
    email varchar(255) not null,
    created_at varchar(19) not null,
    login varchar(255) not null,
    password varchar(255) not null
);

-- round
create table if not exists Round(
    round_id varchar(36) primary key not null unique,
    name varchar(255) not null,
    status int not null
);

-- player
create table if not exists Player(
    user_id int primary key not null,
    role int not null,
    round_id varchar(36) not null
);

-- wonder
create table if not exists Wonder(
    wonder_id varchar(36) primary key not null unique,
    name varchar(255) not null,
    round_id varchar(36) not null,
    is_verified boolean not null,
    is_approved boolean not null,
    created_for_stage int
)