/*Table structure for table `t_global_data` */
DROP TABLE IF EXISTS `t_global_data`;
CREATE TABLE `t_global_data` (
  `type` varchar(20) NOT NULL,
  `data` longtext,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `t_global_data` */
INSERT INTO `t_global_data` VALUES('server_settings','{}');
INSERT INTO `t_global_data` VALUES('pvp_data','[]');
/*Table structure for table `t_pvp_report` */
DROP TABLE IF EXISTS `t_pvp_report`;
CREATE TABLE `t_pvp_report` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `data` longtext NOT NULL,
  `creationTime` BIGINT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB CHARSET=utf8;
/*Data for the table `t_pvp_report` */