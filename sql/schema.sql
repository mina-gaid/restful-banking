# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.16)
# Database: bankapi
# Generation Time: 2016-12-22 16:34:46 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table account
# ------------------------------------------------------------

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` int(11) NOT NULL,
  `sort_code` varchar(120) NOT NULL DEFAULT '',
  `account_number` varchar(255) NOT NULL DEFAULT '',
  `current_balance` decimal(15,4) NOT NULL,
  `account_type` int(11) unsigned NOT NULL,
  `status` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `account_type` (`account_type`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`account_type`) REFERENCES `account_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;

INSERT INTO `account` (`id`, `customer_id`, `sort_code`, `account_number`, `current_balance`, `account_type`, `status`)
VALUES
	(1,1,'6caf3cbd','b787e46f',100.0000,1,0),
	(2,2,'848c0b27','905874d9',9600.0000,2,0),
	(3,3,'fc0b89da','aaba4139',0.0000,1,0),
	(4,4,'98a1fdb2','f33980f5',0.0000,2,1),
	(5,5,'4727fdcd','61080f76',0.0000,1,1),
	(6,5,'01169f49','1a564364',0.0000,2,1),
	(7,6,'be96fd12','6059a948',0.0000,2,1),
	(8,7,'c17107b8','c8cac12a',0.0000,2,1),
	(9,7,'734e9c51','6f569a37',0.0000,1,1),
	(10,8,'87b3757b','6dddbda7',0.0000,1,1);

/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table account_types
# ------------------------------------------------------------

DROP TABLE IF EXISTS `account_types`;

CREATE TABLE `account_types` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_type` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `account_types` WRITE;
/*!40000 ALTER TABLE `account_types` DISABLE KEYS */;

INSERT INTO `account_types` (`id`, `account_type`)
VALUES
	(1,'Current'),
	(2,'Savings');

/*!40000 ALTER TABLE `account_types` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table api_keys
# ------------------------------------------------------------

DROP TABLE IF EXISTS `api_keys`;

CREATE TABLE `api_keys` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `api_key` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `api_keys` WRITE;
/*!40000 ALTER TABLE `api_keys` DISABLE KEYS */;

INSERT INTO `api_keys` (`id`, `api_key`)
VALUES
	(1,'3cf0e880-a782-4ce6-a63c-7ae95891051f');

/*!40000 ALTER TABLE `api_keys` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table customer
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer`;

CREATE TABLE `customer` (
  `customer_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL DEFAULT '',
  `email` varchar(120) NOT NULL DEFAULT '',
  `address` varchar(256) NOT NULL DEFAULT '',
  `password` varchar(256) NOT NULL DEFAULT '',
  `salt` varchar(256) DEFAULT '',
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;

INSERT INTO `customer` (`customer_id`, `name`, `email`, `address`, `password`, `salt`)
VALUES
	(1,'Adam','adhorrig@gmail.com','87 The Way, Hunter\'s Run, Clonsilla, Leinster, Ireland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(2,'Oliwia','oliwia.grunwald@wp.pl','Gdańsk, Poland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(3,'Some','Other@User.com','Gdańsk, Poland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(4,'Blah','Data','Gdansk, Poland','d41d8cd98f00b204e9800998ecf8427e',NULL),
	(5,'test1','test1@gmail.com','Gdańsk, Poland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(6,'test2','test2@gmail.com','Gdańsk, Poland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(7,'test3','test3@gmail.com','Gdańsk, Poland','0945fc9611f55fd0e183fb8b044f1afe',NULL),
	(8,'a','a','Abidjan, Ivory Coast','e��3T�@ӝ�.�\"o{�a','�����!�E');

/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table transaction
# ------------------------------------------------------------

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `transaction_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(255) NOT NULL DEFAULT '',
  `post_balance` decimal(15,4) NOT NULL,
  `customer_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `customer_id` (`customer_id`),
  KEY `account_number` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;

INSERT INTO `transaction` (`transaction_id`, `date`, `description`, `post_balance`, `customer_id`)
VALUES
	(1,'2016-12-21 14:26:13','Transfer',5.0000,1),
	(2,'2016-12-21 15:29:34','Transfer',0.0000,1),
	(3,'2016-12-21 18:03:10','Withdrawal',9600.0000,2),
	(4,'2016-12-21 21:58:22','Lodgement',50.0000,1),
	(5,'2016-12-21 22:05:55','Lodgement',100.0000,1);

/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
