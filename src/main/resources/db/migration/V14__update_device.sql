alter table devices
add column organisation uuid,
add constraint fk_organisation foreign key ("organisation") references organisations(id);