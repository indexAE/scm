-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `operation` varchar(200) NOT NULL COMMENT '操作内容',
  `method` varchar(20) NOT NULL COMMENT '请求方法',
  `params` varchar(500) DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(50) NOT NULL COMMENT 'IP地址',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `ip` varchar(50) NOT NULL COMMENT 'IP地址',
  `status` tinyint(1) NOT NULL COMMENT '状态(1:成功,0:失败)',
  `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) DEFAULT NULL COMMENT '操作系统',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表'; 