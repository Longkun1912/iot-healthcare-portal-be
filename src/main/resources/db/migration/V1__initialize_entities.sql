create table roles (
    id serial primary key,
    name varchar(64)
);

create table organisations(
    id uuid primary key,
    name character varying (100),
    description character varying(500),
    address character varying(200),
    contact_number varchar(11),
    last_updated timestamp
);

create table users (
    id uuid primary key,
    username character varying (100), -- Allow UNICODE
    avatar text,
    email varchar(100),
    mobile varchar(11),
    password varchar(255),
    last_updated timestamp,
    "user_organisation" uuid,
    foreign key ("user_organisation") references organisations(id)
);

create table user_roles (
    user_id uuid references users(id),
    role_id serial references roles(id),
    primary key (user_id,role_id)
);

