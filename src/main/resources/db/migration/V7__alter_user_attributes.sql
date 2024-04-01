alter table users add column gender text;

UPDATE users
SET gender = 'Male'
WHERE id IN (
'8ee65b5a-68a9-11ee-8c99-0242ac120002',
'34761a56-68aa-11ee-8c99-0242ac120002'
);

UPDATE users
SET gender = 'Female'
WHERE id = 'fdd73eda-68e5-11ee-8c99-0242ac120002';

UPDATE users
SET gender = 'Female'
WHERE id = '2ed20910-68ab-11ee-8c99-0242ac120002';


