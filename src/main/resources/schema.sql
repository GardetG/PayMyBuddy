DROP TABLE IF EXISTS `bank_transfer` cascade;
DROP TABLE IF EXISTS `bank_account` cascade;
DROP TABLE IF EXISTS `connection` cascade;
DROP TABLE IF EXISTS `user` cascade;

CREATE TABLE user (
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(60) NOT NULL,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  wallet DECIMAL,
  role INTEGER NOT NULL,
  PRIMARY KEY (user_id)
);

CREATE TABLE bank_account (
  bank_account_id INTEGER NOT NULL AUTO_INCREMENT,
  balance decimal(19,2),
  bic varchar(11) ,
  iban varchar(34) ,
  title varchar(60) ,
  user_id int,
  PRIMARY KEY (bank_account_id)
);

CREATE TABLE connection (
  `user_id` int NOT NULL,
  `connection_id` int NOT NULL
);

CREATE TABLE `bank_transfer` (
  `bank_transfer_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `operation` varchar(255) DEFAULT NULL,
  `bank_account_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`bank_transfer_id`)
);