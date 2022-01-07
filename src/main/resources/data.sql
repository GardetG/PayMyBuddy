INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role)
  values (1, 'admin@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 50, 1);
INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role)
  values (2, 'user@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 50, 0);
INSERT INTO user (user_id, email, password, firstname, lastname, wallet, role)
  values (3, 'user2@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test2', 'test2', 100, 0);

INSERT INTO bank_account (bank_account_id, balance, bic, iban, title, user_id)
    values (1, 100, '12345678abc', '1234567890abcedfghijklmnopqrstu123', 'Primary Account', 3);

INSERT INTO connection (user_id, connection_id)
    values (3,2);