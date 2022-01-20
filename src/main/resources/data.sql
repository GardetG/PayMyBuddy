INSERT INTO user (user_id, email, password, firstname, lastname, balance, role, enabled)
  values (1, 'admin@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 50, 1, 1);
INSERT INTO user (user_id, email, password, firstname, lastname, balance, role, enabled)
  values (2, 'user@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test', 'test', 0, 0, 1);
INSERT INTO user (user_id, email, password, firstname, lastname, balance, role, enabled)
  values (3, 'user2@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'test2', 'test2', 100, 0, 1);

INSERT INTO bank_account (bank_account_id, balance, bic, iban, title, user_id)
    values (1, 100, '1CIN1TjOWlPu/KuEN6y3Hw==', 'cUOSZvErWSPNSDSWRwI6f4Gm9XesJWA/X7YMHtONaemMIALLMyvNEUDajDVW1Bn2', 'Primary Account', 2);
INSERT INTO bank_transfer (bank_transfer_id, amount, date, is_income, bank_account_id)
    values (1, 25, '2022-01-07 20:34:04', 0, 1);

INSERT INTO connection (user_id, connection_id)
    values (3,2);
INSERT INTO connection (user_id, connection_id)
    values (2,3);

INSERT INTO transaction (transaction_id, amount, date, description, emitter_id, receiver_id)
    values (1, 25, '2022-01-07 20:34:04', 'Gift for a friend', 2, 3);