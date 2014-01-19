USE `typhon`;

/*Table structure for table `t_user` */
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `creationTime` bigint(20) DEFAULT NULL,
  `lastAccessedTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `t_role` */

/*Table structure for table `t_role_data` */
DROP TABLE IF EXISTS `t_role_data`;
CREATE TABLE `t_role_data` (
  `rid` int(11) NOT NULL,
  `normalData` text,
  `bagData` text,
  PRIMARY KEY (`rid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*Data for the table `t_role_data` */
