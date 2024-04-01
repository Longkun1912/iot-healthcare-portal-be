CREATE TABLE chats (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    member1 uuid not null,
    member2 uuid not null,
    foreign key (member1) references users(id),
    foreign key (member2) references users(id)
);

CREATE TABLE messages (
    id serial PRIMARY KEY,
    content TEXT,
    sent_at TIMESTAMP,
    sender_id UUID,
    chat_id UUID,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (chat_id) REFERENCES chats(id)
);

insert into chats (id,member1,member2,created_at,updated_at) values
('9f44e831-8b0f-4592-8020-b7f4066cac50','8ee65b5a-68a9-11ee-8c99-0242ac120002','34761a56-68aa-11ee-8c99-0242ac120002',timestamp '2024-01-08 18:35:20',timestamp '2024-01-08 19:35:20'),
('f960c98a-b832-4cef-86e3-b8a14bf44ca9','fdd73eda-68e5-11ee-8c99-0242ac120002','2ed20910-68ab-11ee-8c99-0242ac120002',timestamp '2024-01-08 18:25:20',timestamp '2024-01-08 20:35:20'),
('44ae0748-ffd1-491f-b02c-650b962d9f40','fdd73eda-68e5-11ee-8c99-0242ac120002','8ee65b5a-68a9-11ee-8c99-0242ac120002',timestamp '2024-01-09 18:25:20',timestamp '2024-01-10 20:35:20'),
('510ab99c-283c-4e3a-8a0a-103be5fd24f4','fdd73eda-68e5-11ee-8c99-0242ac120002','34761a56-68aa-11ee-8c99-0242ac120002',timestamp '2024-01-09 19:25:20',timestamp '2024-01-10 21:35:20'),
('bee026f2-6aa1-4401-a2f6-24cba35a0164','34761a56-68aa-11ee-8c99-0242ac120002','2ed20910-68ab-11ee-8c99-0242ac120002',timestamp '2024-01-10 19:25:20',timestamp '2024-01-11 21:35:20');

insert into messages(content,sent_at,sender_id,chat_id) values
('Hello',timestamp '2024-01-08 18:35:20','8ee65b5a-68a9-11ee-8c99-0242ac120002','9f44e831-8b0f-4592-8020-b7f4066cac50'),
('Hi',timestamp '2024-01-08 18:36:20','34761a56-68aa-11ee-8c99-0242ac120002','9f44e831-8b0f-4592-8020-b7f4066cac50'),
('How are you?',timestamp '2024-01-08 18:37:20','8ee65b5a-68a9-11ee-8c99-0242ac120002','9f44e831-8b0f-4592-8020-b7f4066cac50'),
('I am fine',timestamp '2024-01-08 18:38:20','34761a56-68aa-11ee-8c99-0242ac120002','9f44e831-8b0f-4592-8020-b7f4066cac50'),

('Hello',timestamp '2024-01-08 19:37:20','fdd73eda-68e5-11ee-8c99-0242ac120002','f960c98a-b832-4cef-86e3-b8a14bf44ca9'),
('Hi',timestamp '2024-01-08 19:45:20','2ed20910-68ab-11ee-8c99-0242ac120002','f960c98a-b832-4cef-86e3-b8a14bf44ca9'),

('How are you?',timestamp '2024-01-09 18:35:20','fdd73eda-68e5-11ee-8c99-0242ac120002','44ae0748-ffd1-491f-b02c-650b962d9f40'),
('I am fine',timestamp '2024-01-09 18:38:20','8ee65b5a-68a9-11ee-8c99-0242ac120002','44ae0748-ffd1-491f-b02c-650b962d9f40'),

('Hello',timestamp '2024-01-09 19:37:20','fdd73eda-68e5-11ee-8c99-0242ac120002','510ab99c-283c-4e3a-8a0a-103be5fd24f4'),
('Hi',timestamp '2024-01-09 19:45:20','34761a56-68aa-11ee-8c99-0242ac120002','510ab99c-283c-4e3a-8a0a-103be5fd24f4'),

('How are you?',timestamp '2024-01-10 19:35:20','34761a56-68aa-11ee-8c99-0242ac120002','bee026f2-6aa1-4401-a2f6-24cba35a0164'),
('I am fine',timestamp '2024-01-10 19:38:20','2ed20910-68ab-11ee-8c99-0242ac120002','bee026f2-6aa1-4401-a2f6-24cba35a0164');

