DROP SCHEMA IF EXISTS `paymybuddy`;
CREATE SCHEMA IF NOT EXISTS `paymybuddy`;

USE `paymybuddy`;

DROP TABLE IF EXISTS `user` cascade;
CREATE TABLE `user` (
  `user_id` INTEGER NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(8,2) DEFAULT 0.00,
  `email` VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(60) NOT NULL,
  `firstname` VARCHAR(50) NOT NULL,
  `lastname` VARCHAR(50) NOT NULL,
  `role` INTEGER NOT NULL,
  `registration_date` DATETIME,
  `enabled` BIT(1) DEFAULT TRUE,
  PRIMARY KEY (`user_id`)
);

DROP TABLE IF EXISTS `bank_account` cascade;
CREATE TABLE `bank_account` (
  `bank_account_id` INTEGER NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(8,2) DEFAULT 0.00,
  `bic` VARCHAR(24) NOT NULL,
  `iban` VARCHAR(64) NOT NULL,
  `title` VARCHAR(60) NOT NULL,
  `user_id` INTEGER NOT NULL,
  PRIMARY KEY (`bank_account_id`)
);

DROP TABLE IF EXISTS `connection`;
CREATE TABLE `connection` (
  `user_id` INTEGER NOT NULL,
  `connection_id` INTEGER NOT NULL,
  PRIMARY KEY (`user_id`,`connection_id`)
);

DROP TABLE IF EXISTS `bank_transfer`;
CREATE TABLE `bank_transfer` (
  `bank_transfer_id` int NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(5,2) DEFAULT 0.00,
  `date` DATETIME DEFAULT NULL,
  `is_income` bit(1) DEFAULT NULL,
  `bank_account_id` INTEGER NOT NULL,
  PRIMARY KEY (`bank_transfer_id`)
);

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `transaction_id` INTEGER NOT NULL AUTO_INCREMENT,
  `amount` DECIMAL(5,2) DEFAULT 0.00,
  `date` DATETIME DEFAULT NULL,
  `description` varchar(80) DEFAULT NULL,
  `emitter_id` INTEGER DEFAULT NULL,
  `receiver_id` INTEGER DEFAULT NULL,
  PRIMARY KEY (`transaction_id`)
);