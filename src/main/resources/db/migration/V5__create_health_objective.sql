create table health_objectives (
    id serial primary key,
    title text,
    image text,
    heart_rate int,
    temperature int,
    description text
);

ALTER TABLE users
ADD COLUMN health_objective_id int;

ALTER TABLE users
ADD CONSTRAINT fk_health_objective
FOREIGN KEY (health_objective_id)
REFERENCES health_objectives(id);

