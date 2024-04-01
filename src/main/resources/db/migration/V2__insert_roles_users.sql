---
---Insert to roles entity
---
insert into roles (name) values ('manager'),('admin'),('user');


---
---Insert to organisation entity
---
insert into organisations (id, name, description, address, contact_number, last_updated) values
('ce1d590c-6e64-11ee-b962-0242ac120002', 'Samsung', 'Samsung is a global conglomerate with a substantial presence in IoT. They produce a wide range of IoT devices such as smart refrigerators, smart TVs, and smart home appliances that can be interconnected for a seamless smart home experience. Additionally, Samsung SmartThings platform provides a hub for managing and controlling IoT devices within a home environment.', 'Số 2, đường Hải Triều, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh', '02839157310', timestamp '2023-10-22 21:21:08'),
('d4c224cc-6e64-11ee-b962-0242ac120002', 'Apple', 'Apple, renowned for its consumer electronics, has a growing stake in IoT through its HomeKit framework. HomeKit enables Apple users to connect and control various IoT devices, from smart thermostats to smart locks, using their iOS devices. Apple privacy and security emphasis is a significant factor in its IoT strategy.', 'Phòng 901, Ngôi Nhà Đức Tại Tp. Hồ Chí Minh, số 33, đường Lê Duẩn, Phường Bến Nghé, Quận 1, thành phố Hồ Chí Minh, Việt Nam', '1800 1192', timestamp '2023-10-22 21:21:08'),
('def4cee0-6e64-11ee-b962-0242ac120002', 'Toshiba', 'Toshiba, primarily known for its electronics and industrial solutions, has been exploring IoT applications in various sectors. They have IoT offerings for businesses, including solutions for manufacturing, healthcare, and energy management. Toshiba IoT technologies often focus on optimizing industrial processes and improving efficiency.', 'Số 12, Đường 15, Khu phố 4, phường Linh Trung, Tp. Thủ Đức, Tp.HCM', '02838242818', timestamp '2023-10-22 21:21:08');


---
---Insert to users entity
---
insert into users (id,username,avatar,email,mobile,password,last_updated,user_organisation) values

--- Email: longphgbh200168@fpt.edu.vn - Password: Longhoang1912$ - Role: manager,admin, user
('8ee65b5a-68a9-11ee-8c99-0242ac120002','Phạm Hoàng Long','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAeP2o508M89xkDAfVKDKysH3l0Pheuwe_oBuawt0sPIeW9fuoy-LhAnc4HQmyanKubY8&usqp=CAU','longphgbh200168@fpt.edu.vn','0396681233','$2a$12$HREi9U2E.4RMre2kZKtjOuy443Jf0aNwBZfT46eA1QZHignXcYtu6',timestamp '2023-10-12 09:50:08','ce1d590c-6e64-11ee-b962-0242ac120002'),

--- Email: khanhntngch210731@fpt.edu.vn - Password: KhanhNguyen5678# - Role: admin, user
('34761a56-68aa-11ee-8c99-0242ac120002','Nguyễn Trần Nam Khánh','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRuwIuh06JFijn9SV63EnwPqf0GO3ELXur9ZFXiTS_x00lUZt12eT-X0JeSPh0n_R8vlyo&usqp=CAU','khanhntngch210731@fpt.edu.vn','0971824696','$2a$12$XvHCV6KazwATozktLy.78OH/BSNfWbfN5YbTYObOpMzitROkjlnMC',timestamp '2023-10-12 10:40:00','d4c224cc-6e64-11ee-b962-0242ac120002'),

--- Email: hungcuong28597@gmail.com - Password: Thienthansanga&7007& - Role: user
('fdd73eda-68e5-11ee-8c99-0242ac120002','Phan Hùng Cường','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSk7Enb_6qy5OZ0s817mTY3kboZCM-Jqcj9yQ&usqp=CAU','hungcuong28597@gmail.com','0337877040','$2a$12$irX1zJvVQJ.hpd3BPzBb/ut5BdUwLvCHTkaCKmVaegi1SAa9X4q4W',timestamp '2023-10-12 14:20:10','ce1d590c-6e64-11ee-b962-0242ac120002'),

--- Email: andtrinh3189@gmail.com - Password: Hackerman#0553% - Role: user
('2ed20910-68ab-11ee-8c99-0242ac120002','Trịnh Đức Anh','https://freepngimg.com/thumb/businessman/22110-4-old-businessman.png','andtrinh3189@gmail.com','0356590672','$2a$12$RmP4mU2BXjwIp4bj05sYWOLj1vH4lm9AFYx2pT/13sSMsAEds2X1a',timestamp '2023-10-12 18:35:20','def4cee0-6e64-11ee-b962-0242ac120002');

insert into user_roles (user_id,role_id) values
('8ee65b5a-68a9-11ee-8c99-0242ac120002',1),
('8ee65b5a-68a9-11ee-8c99-0242ac120002',2),
('8ee65b5a-68a9-11ee-8c99-0242ac120002',3),

('34761a56-68aa-11ee-8c99-0242ac120002',2),
('34761a56-68aa-11ee-8c99-0242ac120002',3),

('fdd73eda-68e5-11ee-8c99-0242ac120002',3),
('2ed20910-68ab-11ee-8c99-0242ac120002',3);

