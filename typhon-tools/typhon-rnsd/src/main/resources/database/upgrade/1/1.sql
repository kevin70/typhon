USE `rnsd`;

/*Table structure for table `t_region` */

DROP TABLE IF EXISTS `t_region`;

CREATE TABLE `t_region` (
  `rid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(90) CHARACTER SET utf8 DEFAULT NULL,
  `ip` varchar(90) CHARACTER SET utf8 DEFAULT NULL,
  `port` int(6) DEFAULT NULL,
  `jmxPort` int(6) DEFAULT NULL,
  `os` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `state` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `openningTime` bigint(20) DEFAULT NULL,
  `creationTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `t_global_settings` */
DROP TABLE IF EXISTS `t_global_settings`;

CREATE TABLE `t_global_settings` (
  `key` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf8 DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `t_recharging` */
DROP TABLE IF EXISTS `t_recharging`;

CREATE TABLE `t_recharging` (
  `tradeId` VARCHAR (50) NOT NULL,
  `platform` VARCHAR (15) NOT NULL,
  `uid` VARCHAR (50),
  `region` VARCHAR (20),
  `goods` VARCHAR (20),
  `amount` INT(11),
  `creationTime` BIGINT(20),
  `status` VARCHAR (10),
  `channel` VARCHAR (50),
  PRIMARY KEY (`tradeId`, `platform`)
) CHARSET = utf8 COLLATE = utf8_bin ;

-- 初始版本号
INSERT INTO `t_global_settings` (`key`, `value`) VALUES ('region.android.version', '1.0'); 
INSERT INTO `t_global_settings` (`key`, `value`) VALUES ('region.ios.version', '1.0'); 
INSERT INTO `t_global_settings` (`key`, `value`) VALUES ('region.win.version', '1.0'); 