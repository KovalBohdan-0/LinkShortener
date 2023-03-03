INSERT INTO roles (code, name) VALUES ('USER', 'User group'), ('ADMIN','Admin group') ;
INSERT INTO users(email, password) VALUES ('Admin', '$2a$10$fXH7.zfzD2PxCeScpPRCTeywWXfxS8Gc5QxwMsaLD9lyRtY2Z4Xnq');
INSERT INTO users(email, password) VALUES ('anonymousUser', '$2a$10$aJ5ya3tv2MMbQ0jW0oISte039pjiatFqlFikwu0oR1AIQcGtLjpWi');
INSERT INTO user_roles(user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles(user_id, role_id) VALUES (2, 1);
INSERT INTO links (full_link, alias, user_id) VALUES ('fullLink', 'alias', 1);