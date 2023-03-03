INSERT INTO roles (code, name) VALUES ('USER', 'User group'), ('ADMIN','Admin group') ON CONFLICT DO NOTHING;
INSERT INTO users(id, email, password) VALUES (1, 'Admin', '$2a$10$fXH7.zfzD2PxCeScpPRCTeywWXfxS8Gc5QxwMsaLD9lyRtY2Z4Xnq') ON CONFLICT DO NOTHING;
INSERT INTO users(id, email, password) VALUES (2, 'anonymousUser', '$2a$10$aJ5ya3tv2MMbQ0jW0oISte039pjiatFqlFikwu0oR1AIQcGtLjpWi') ON CONFLICT DO NOTHING;
INSERT INTO user_roles(user_id, role_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2) ON CONFLICT DO NOTHING;

SELECT setval('users_id_seq', max(id)) FROM users;
