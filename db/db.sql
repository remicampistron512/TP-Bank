-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           12.1.2-MariaDB - MariaDB Server
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Listage de la structure de la base pour tp_bank
CREATE DATABASE IF NOT EXISTS `tp_bank` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `tp_bank`;

-- Listage de la structure de table tp_bank. accounts
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_number` varchar(11) NOT NULL,
  `customer_id` bigint(20) unsigned NOT NULL,
  `balance` decimal(19,2) NOT NULL DEFAULT 0.00,
  `overdraft_limit` decimal(19,2) NOT NULL DEFAULT 0.00,
  `daily_withdrawal_limit` decimal(19,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_accounts_account_number` (`account_number`),
  KEY `fk_accounts_customer` (`customer_id`),
  CONSTRAINT `fk_accounts_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`user_id`),
  CONSTRAINT `ck_overdraft_nonneg` CHECK (`overdraft_limit` >= 0),
  CONSTRAINT `ck_daily_limit_nonneg` CHECK (`daily_withdrawal_limit` >= 0),
  CONSTRAINT `ck_account_number_format` CHECK (`account_number` regexp '^FR-[0-9]{4}-[0-9]{4}$')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table tp_bank.accounts : ~0 rows (environ)
DELETE FROM `accounts`;

-- Listage de la structure de table tp_bank. advisors
CREATE TABLE IF NOT EXISTS `advisors` (
  `user_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_advisors_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table tp_bank.advisors : ~0 rows (environ)
DELETE FROM `advisors`;

-- Listage de la structure de table tp_bank. customers
CREATE TABLE IF NOT EXISTS `customers` (
  `user_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_customers_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table tp_bank.customers : ~1 rows (environ)
DELETE FROM `customers`;
INSERT INTO `customers` (`user_id`) VALUES
	(1);

-- Listage de la structure de table tp_bank. operations
CREATE TABLE IF NOT EXISTS `operations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ts` timestamp NOT NULL DEFAULT current_timestamp(),
  `type` enum('DEPOSIT','WITHDRAWAL','TRANSFER') NOT NULL,
  `status` enum('PENDING','COMPLETED','FAILED') NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `source_account_id` bigint(20) unsigned DEFAULT NULL,
  `target_account_id` bigint(20) unsigned DEFAULT NULL,
  `executed_by_advisor_id` bigint(20) unsigned DEFAULT NULL,
  `failure_reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ops_advisor` (`executed_by_advisor_id`),
  KEY `idx_ops_source_ts` (`source_account_id`,`ts` DESC),
  KEY `idx_ops_target_ts` (`target_account_id`,`ts` DESC),
  KEY `idx_ops_status` (`status`),
  CONSTRAINT `fk_ops_advisor` FOREIGN KEY (`executed_by_advisor_id`) REFERENCES `advisors` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_ops_source_account` FOREIGN KEY (`source_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `fk_ops_target_account` FOREIGN KEY (`target_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `ck_amount_positive` CHECK (`amount` > 0),
  CONSTRAINT `ck_operation_accounts_by_type` CHECK (`type` = 'DEPOSIT' and `source_account_id` is null and `target_account_id` is not null or `type` = 'WITHDRAWAL' and `source_account_id` is not null and `target_account_id` is null or `type` = 'TRANSFER' and `source_account_id` is not null and `target_account_id` is not null and `source_account_id` <> `target_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table tp_bank.operations : ~0 rows (environ)
DELETE FROM `operations`;

-- Listage de la structure de table tp_bank. users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table tp_bank.users : ~1 rows (environ)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `first_name`, `last_name`, `created_at`) VALUES
	(1, 'bobby', 'bob', '2025-12-19 14:17:22');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
