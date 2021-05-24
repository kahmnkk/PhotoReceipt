CREATE DATABASE IF NOT EXISTS `db_master`;
USE `db_master`;

DROP TABLE IF EXISTS `tb_account_info`;
CREATE TABLE `tb_account_info` (
  `idx` bigint(20) unsigned NOT NULL,
  `id` varchar(254) NOT NULL,
  `pw` varchar(200) NOT NULL,
  `salt` varchar(50) NOT NULL,
  `status` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Account Info';

DROP TABLE IF EXISTS `tb_board_info`;
CREATE TABLE `tb_board_info` (
  `idx` bigint(20) unsigned NOT NULL,
  `owner` bigint(20) NOT NULL,
  `imgLink` varchar(500) NOT NULL,
  `filter` json DEFAULT NULL,
  `text` text NOT NULL,
  `status` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Board Info';

DROP TABLE IF EXISTS `tb_board_like`;
CREATE TABLE `tb_board_like` (
  `boardIdx` bigint(20) unsigned NOT NULL,
  `userIdx` bigint(20) NOT NULL,
  `status` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`boardIdx`,`userIdx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Board Like';
