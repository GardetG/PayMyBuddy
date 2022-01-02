TRUNCATE TABLE `user`;
TRUNCATE TABLE `role`;

INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role_id)
  values (1, 'admin@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 50, 2);
INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role_id)
  values (2, 'user@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 50, 1);
INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role_id)
  values (3, 'user2@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test2', 'test2', 100, 1);

INSERT INTO role (role_id, name)
    values (1, 'USER');
INSERT INTO role (role_id, name)
    values (2, 'ADMIN');