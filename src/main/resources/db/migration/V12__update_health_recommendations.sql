alter table health_recommendations
alter column heart_rate_impact type text;

update health_recommendations set heart_rate_impact = 'High'
where id = 1;

update health_recommendations set heart_rate_impact = 'Low'
where id = 2;

update health_recommendations set heart_rate_impact = 'High'
where id = 3;

update health_recommendations set heart_rate_impact = 'Stable'
where id = 4;

update health_recommendations set heart_rate_impact = 'Low'
where id = 5;