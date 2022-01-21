INSERT INTO user (user_id, email, password, firstname, lastname, balance, role, registration_date, enabled)
  values (2, 'user1@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'User1', 'test', 100, 0, '2000-01-01 00:00:00', 1);
INSERT INTO user (user_id, email, password, firstname, lastname, balance, role, registration_date, enabled)
  values (3, 'user2@mail.com', '$2a$10$OSzSdG3ou.NCsuZKUJOQOuly.K64NMYyoHAi4psrzdFfSBZpdoggy', 'User2', 'test', 0, 0, '2000-01-01 00:00:00', 1);

INSERT INTO bank_account (bank_account_id, balance, bic, iban, title, user_id)
    values (1, 100, '1CIN1TjOWlPu/KuEN6y3Hw==', 'cUOSZvErWSPNSDSWRwI6f4Gm9XesJWA/X7YMHtONaemMIALLMyvNEUDajDVW1Bn2', 'Primary Account', 3);
INSERT INTO bank_transfer (bank_transfer_id, amount, date, is_income, bank_account_id)
    values (1, 25, '2000-01-02 00:00:00', 0, 1);

INSERT INTO connection (user_id, connection_id)
    values (3,2);
INSERT INTO connection (user_id, connection_id)
    values (2,3);

INSERT INTO transaction (transaction_id, amount, date, description, emitter_id, receiver_id)
    values (1, 25, '2000-01-02 00:00:00', 'Gift for a friend', 3, 2);