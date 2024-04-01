alter table health_records add column blood_pressure int;
alter table health_recommendations add column blood_pressure_impact text;

alter table health_objectives
add column blood_pressure int,
ALTER COLUMN temperature TYPE float USING temperature::float;

update health_records set blood_pressure = 120
where id in
('00446444-49fa-4a7c-8e9f-6adc0d11a45c',
'029528f2-5d67-4d8e-a28c-3289c0a6b62f',
'14cd9074-6a56-4764-b027-a18d27e6d1ac',
'3a8191d0-8eb7-41c7-a0be-2b49416d7c62',
'3a8946da-9a0a-431a-8116-25f8127251c1',
'415949d7-01a7-4e96-9863-b2e1586055b7',
'4c2d77c2-8178-4248-9f8a-ef626b8f2c2d',
'50903cd1-7b2f-442c-9cba-426e9b836de4',
'5305a1c3-9425-48eb-ba51-9f27fd0ecae8',
'5497879e-d266-47ba-8ca5-96a25acb172b');

update health_records set blood_pressure = 130
where id in
('1359e38d-b24d-46cc-9fe3-5ecedb115ad9',
'162000f2-a2ab-4a97-b75a-ee1368202f72',
'25d7b8a7-7bbb-455f-8b23-0a9a8ef52d99',
'3f1a74d5-f4f2-47db-9383-dca7813ee8ea',
'47d43930-f552-43e7-bfaf-c24d4af8674a',
'517c3080-41d2-44e3-b99c-abad4a2192f4');

update health_records set blood_pressure = 90
where id in
('555c8c91-b810-4b40-acc8-a7b0f2c35e0e',
'5deb7afd-2b71-4e10-8769-f72383c8a171',
'64b34977-980a-4d14-9642-a9fd4ab67dc2',
'78d4e9a1-5d03-4995-974c-6814ffed68c9');

update health_recommendations set blood_pressure_impact = 'Increase'
where id in
(1,2,3);

update health_recommendations set blood_pressure_impact = 'Decrease'
where id =4;

update health_recommendations set blood_pressure_impact = 'Maintain'
where id =5;

update health_objectives set blood_pressure = 120 where id = 1;
update health_objectives set blood_pressure = 130 where id = 2;
update health_objectives set blood_pressure = 90 where id = 3;

