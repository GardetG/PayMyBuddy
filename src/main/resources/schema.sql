DROP TABLE IF EXISTS `user` cascade;
CREATE TABLE user (
  user_id INTEGER NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(60) NOT NULL,
  firstname VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  wallet DECIMAL,
  role_id INTEGER NOT NULL,
  PRIMARY KEY (user_id)
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE role (
  role_id INTEGER NOT NULL AUTO_INCREMENT,
  name VARCHAR(10) NOT NULL,
  PRIMARY KEY (role_id)
);

DROP TABLE IF EXISTS `bank_account`;
CREATE TABLE bank_account (
  bank_account_id INTEGER NOT NULL AUTO_INCREMENT,
  balance decimal(19,2),
  bic varchar(11) ,
  iban varchar(34) ,
  title varchar(60) ,
  user_id int,
  PRIMARY KEY (bank_account_id)
);