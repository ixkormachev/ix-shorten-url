INSERT INTO `role` VALUES (1,'ADMIN');
INSERT INTO user (user_id, email, password, name, last_name, active) VALUES (1, 'test2@test.com','$2a$10$umVniRJiNbOC5EeAB3Ef0.RCwvG9PIuvwcOeFDg/qJPFtciFgNQKy', 'user2', 'lastName2', 1);
INSERT INTO `user_role` VALUES (1, 1);
