alter table health_objectives add column information_url text;

---
---Insert to health_objectives entity
---
insert into health_objectives (title, image, heart_rate, temperature, description, information_url) values
('Cardiovascular Fitness',
'https://res.cloudinary.com/dokyaftrm/image/upload/v1705584124/iot-web-portal/health-objectives/cardiovascular_fitness.png', 60, 37,
'Cardiovascular fitness is how well your body takes in oxygen and delivers it to your muscles and organs during prolonged periods of exercise.
It encompasses a wide range of physical adaptations.',
'https://sportscienceinsider.com/cardiovascular-fitness/#:~:text=The%20best%20definition%20of%20cardiovascular%20fitness%20is%20as,fitness%20encompasses%20a%20wide%20range%20of%20physical%20adaptations.'),

('Muscular Strength',
'https://res.cloudinary.com/dokyaftrm/image/upload/v1705584415/iot-web-portal/health-objectives/muscular_strength.png', 80, 37,
'Muscular strength is the maximal amount of force that a muscle or group of muscles can generate at one time.
It is a critical component of fitness, often measured during weightlifting or when a person performs an isometric exercise, such as a push-up.',
'https://www.athleticinsight.com/sports-terminologies/muscular-strength'),

('Healthy Hydration',
'https://askthescientists.com/wp-content/uploads/2021/03/AdobeStock_210055930.jpeg', 70, 37,
'Healthy hydration helps protect delicate bones, your brain, spine, and other vital organs.
Spinal fluid, the fluid between joints, and the space around organs is made up largely of water.
This liquid acts as a shock absorber and a barrier, protecting your body from damage caused by impact.',
'https://askthescientists.com/hydration/');
