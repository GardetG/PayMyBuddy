DROP TABLE IF EXISTS `user` cascade;
CREATE TABLE user (
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(60) NOT NULL,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  balance DECIMAL(8,2),
  role INTEGER NOT NULL,
  registration_date DATETIME,
  enabled bit(1),
  PRIMARY KEY (user_id)
);

DROP TABLE IF EXISTS `bank_account` cascade;
CREATE TABLE bank_account (
  bank_account_id INTEGER NOT NULL AUTO_INCREMENT,
  balance DECIMAL(8,2),
  bic VARCHAR(24) ,
  iban VARCHAR(64) ,
  title VARCHAR(60) ,
  user_id INTEGER NOT NULL,
  PRIMARY KEY (bank_account_id)
);

DROP TABLE IF EXISTS `connection`;
CREATE TABLE connection (
  user_id INTEGER NOT NULL,
  connection_id INTEGER NOT NULL
);

DROP TABLE IF EXISTS `bank_transfer`;
CREATE TABLE `bank_transfer` (
  bank_transfer_id INTEGER NOT NULL AUTO_INCREMENT,
  amount decimal(5,2) DEFAULT NULL,
  date DATETIME DEFAULT NULL,
  is_income BIT(1),
  bank_account_id INTEGER NOT NULL,
  PRIMARY KEY (`bank_transfer_id`)
);

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  transaction_id int NOT NULL AUTO_INCREMENT,
  amount decimal(5,2) DEFAULT NULL,
  date datetime DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  emitter_id int DEFAULT NULL,
  receiver_id int DEFAULT NULL,
  PRIMARY KEY (`transaction_id`)
);