/*Table structure for table `t_user` */
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `creationTime` bigint(20) DEFAULT NULL,
  `lastAccessedTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) AUTO_INCREMENT = 10000 ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `t_user` */

/*Table structure for table `t_role` */
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `rid` int(11) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `level` int(3) DEFAULT NULL,
  `enabled` boolean DEFAULT TRUE,
  `creationTime` bigint(20) DEFAULT NULL,
  `lastAccessedTime` bigint(20) DEFAULT NULL,
  `diamond` int(11) NOT NULL,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `t_role` */

/*Table structure for table `t_role_data` */
DROP TABLE IF EXISTS `t_role_data`;
CREATE TABLE `t_role_data` (
  `rid` int(11) NOT NULL,
  `normalData` longtext,
  `bagData` longtext,
  `heroBagData` longtext,
  `vacantData` longtext,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `t_role_data` */

/*Table structure for table `t_event` */

DROP TABLE IF EXISTS `t_event`;

CREATE TABLE `t_event` (
  `pid` bigint(20) NOT NULL AUTO_INCREMENT,
  `uid` bigint(20) DEFAULT NULL,
  `eventName` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `creationTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Data for the table `t_event` */

