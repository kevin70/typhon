DROP TABLE IF EXISTS `t_zucks`;
CREATE TABLE `t_zucks` (
  `zid` VARCHAR (255) NOT NULL,
  `os` VARCHAR (20) NOT NULL,
  `point` INT,
  `uid` VARCHAR (255),
  PRIMARY KEY (`zid`, `os`)
) ENGINE = INNODB CHARSET = utf8 COLLATE = utf8_bin ;