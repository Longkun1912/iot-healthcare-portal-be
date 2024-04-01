create table devices (
    id uuid primary key,
    name character varying (500),
    image text,
    application_type text,
    status text,
    description text,
    last_updated timestamp,
    "device_owner" uuid,
    foreign key ("device_owner") references users(id)
);

insert into devices (id,name,image,application_type,status,description,last_updated,device_owner) values
('f886f618-82c2-11ee-b962-0242ac120002','Smart Watch DK05 Color Screen Smart Watch Sleep Heart Rate Blood Pressure Monitoring Multi-Language Intelligent Sports Bracelet','https://www.dhresource.com/0x0/f2/albu/g9/M01/34/AF/rBVaWF5SqYyADI3qAAEmfFV34vY792.jpg',
'Smart Watch','Active','Smart watch for measuring footsteps, hearth rate and temperature.',timestamp '2023-10-12 18:35:20','fdd73eda-68e5-11ee-8c99-0242ac120002');