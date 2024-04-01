create table health_records (
    id uuid primary key,
    heart_rate int,
    temperature float,
    last_updated timestamp,
    user_id uuid not null,
    foreign key (user_id) references users (id)
);

create table health_recommendations (
    id serial primary key,
    name varchar(64),
    heart_rate_impact int,
    temperature_impact text,
    description text,
    guide_link text
);

---
---Insert to health_records entity
---
insert into health_records (id, heart_rate, temperature, last_updated, user_id) values
('e3b9c7e0-9e3a-4c9a-8a61-3f4f7e9a7a9a', 80, 36.5, timestamp '2024-01-05 07:20:00','34761a56-68aa-11ee-8c99-0242ac120002'),
('fd1012f8-beab-4172-b904-208634fda890', 90, 37.5, timestamp '2024-01-06 12:28:06','34761a56-68aa-11ee-8c99-0242ac120002'),
('5497879e-d266-47ba-8ca5-96a25acb172b', 70, 36.9, timestamp '2024-01-07 17:46:30','34761a56-68aa-11ee-8c99-0242ac120002'),
('7b467b46-0166-4e81-8f51-8f8898115f99', 110, 36.5, timestamp '2024-01-08 21:00:10','34761a56-68aa-11ee-8c99-0242ac120002'),
('f86a91c1-ec68-4069-92f5-f1ef392c7b0b', 100, 36.3, timestamp '2024-01-09 09:33:02','34761a56-68aa-11ee-8c99-0242ac120002'),

('64b34977-980a-4d14-9642-a9fd4ab67dc2', 78, 38.7, timestamp '2024-01-09 16:25:40','8ee65b5a-68a9-11ee-8c99-0242ac120002'),
('db42e5d2-2d58-49e0-90d0-a9a5e4eed3cb', 68, 39.2, timestamp '2024-01-10 09:20:05','8ee65b5a-68a9-11ee-8c99-0242ac120002'),
('25d7b8a7-7bbb-455f-8b23-0a9a8ef52d99', 100, 38.5, timestamp '2024-01-11 08:55:33','8ee65b5a-68a9-11ee-8c99-0242ac120002'),
('50903cd1-7b2f-442c-9cba-426e9b836de4', 100, 38.1, timestamp '2024-01-12 11:11:23','8ee65b5a-68a9-11ee-8c99-0242ac120002'),
('555c8c91-b810-4b40-acc8-a7b0f2c35e0e', 94, 37.8, timestamp '2024-01-13 14:20:00','8ee65b5a-68a9-11ee-8c99-0242ac120002'),

('b988ae3e-a840-4bc1-a44a-de4178823120', 125, 37.7, timestamp '2024-01-08 22:24:48','2ed20910-68ab-11ee-8c99-0242ac120002'),
('78d4e9a1-5d03-4995-974c-6814ffed68c9', 131, 37.3, timestamp '2024-01-09 10:15:04','2ed20910-68ab-11ee-8c99-0242ac120002'),
('415949d7-01a7-4e96-9863-b2e1586055b7', 131, 37.4, timestamp '2024-01-10 16:11:42','2ed20910-68ab-11ee-8c99-0242ac120002'),
('029528f2-5d67-4d8e-a28c-3289c0a6b62f', 129, 37.8, timestamp '2024-01-11 19:54:07','2ed20910-68ab-11ee-8c99-0242ac120002'),
('ee813c13-0b85-464a-ae61-4af5a2a429c9', 127, 38.0, timestamp '2024-01-12 17:36:39','2ed20910-68ab-11ee-8c99-0242ac120002'),

('14cd9074-6a56-4764-b027-a18d27e6d1ac', 83, 35.7, timestamp '2024-01-06 13:00:48','fdd73eda-68e5-11ee-8c99-0242ac120002'),
('517c3080-41d2-44e3-b99c-abad4a2192f4', 90, 36.1, timestamp '2024-01-07 12:04:06','fdd73eda-68e5-11ee-8c99-0242ac120002'),
('5deb7afd-2b71-4e10-8769-f72383c8a171', 90, 36.2, timestamp '2024-01-08 21:22:25','fdd73eda-68e5-11ee-8c99-0242ac120002'),
('c1b53a01-23d8-4192-a4e9-a4995ff88f5e', 94, 36.6, timestamp '2024-01-09 20:59:02','fdd73eda-68e5-11ee-8c99-0242ac120002'),
('d126152a-d05e-4934-b7f8-0b80b9eedab2', 89, 36.5, timestamp '2024-01-10 18:10:49','fdd73eda-68e5-11ee-8c99-0242ac120002');

---
---Insert to health_recommendations entity
---
insert into health_recommendations (name, heart_rate_impact, temperature_impact, description, guide_link) values
('High-Intensity Interval Training', 80, 'Increase',
'High-intensity interval training (HIIT) is a training protocol alternating short periods of intense or explosive anaerobic exercise with brief recovery periods until the point of exhaustion.
 HIIT involves exercises performed in repeated quick bursts at maximum or near maximal effort with periods of rest or low activity between bouts.',
 'https://www.menshealth.com/fitness/a25424850/best-hiit-exercises-workout'),

('Cold Weather Running', 90, 'Decrease',
'Running in cold weather is a great way to get your exercise in, but it can also be dangerous if you are not careful.
If you are not careful, you can get frostbite, hypothermia, or even worse.',
'https://www.rei.com/learn/expert-advice/winter-running-tips.html'),

('Circuit Training', 70, 'Increase',
'Circuit training is a form of body conditioning or endurance training or resistance training using high-intensity.',
'https://thebarbell.com/circuit-training/'),

('Yoga', -70, 'Unchanged',
'Yoga is a group of physical, mental, and spiritual practices or disciplines which originated in ancient India.',
'https://www.healthline.com/health/fitness-exercise/definitive-guide-to-yoga'),

('Meditation', -40, 'Unchanged',
'Meditation is a practice where an individual uses a technique – such as mindfulness, or focusing the mind on a particular object, thought, or activity – to train attention and awareness, and achieve a mentally clear and emotionally calm and stable state.',
'https://www.mindful.org/how-to-meditate/');







