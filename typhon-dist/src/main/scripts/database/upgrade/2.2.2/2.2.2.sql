INSERT INTO `t_global_data` VALUES('pve_difficult_data', '[]') ;
INSERT INTO `t_global_data` VALUES('heroStar_data', '[]') ; 

ALTER TABLE `t_user` ADD COLUMN `platform` VARCHAR (15) NULL AFTER `lastAccessedTime` ;

DROP TABLE IF EXISTS `t_cdkey` ;
CREATE TABLE `t_cdkey` (
 `cdkey` VARCHAR (20) NOT NULL,
 `batch` INT (4) DEFAULT NULL,
 `itemId` VARCHAR (8) NOT NULL,
 `beginTime` BIGINT (20) NOT NULL,
 `endTime` BIGINT (20) NOT NULL,
 `state` INT (4) NOT NULL,
 `platform` VARCHAR (15) DEFAULT NULL,
 `creationTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`cdkey`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;

DROP TABLE IF EXISTS `t_plog` ;
CREATE TABLE `t_plog` (
 `uid` INT (11) NOT NULL,
 `changeValue` INT (10) NOT NULL,
 `changeType` VARCHAR (10) NOT NULL,
 `description` LONGTEXT,
 `creationTime` BIGINT (20) DEFAULT NULL
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;
CREATE INDEX `uid` ON `t_plog` (`uid`)