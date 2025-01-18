CREATE TABLE IF NOT EXISTS `settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `system_name` varchar(100) NOT NULL COMMENT '系统名称',
  `system_desc` varchar(500) DEFAULT NULL COMMENT '系统描述',
  `password_min_length` int(11) DEFAULT 6 COMMENT '密码最小长度',
  `login_lock_enabled` tinyint(1) DEFAULT 0 COMMENT '是否启用登录锁定',
  `operation_log_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用操作日志',
  `login_log_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用登录日志',
  `show_welcome` tinyint(1) DEFAULT 1 COMMENT '是否显示欢迎语',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 插入默认设置
INSERT INTO `settings` (system_name, system_desc, password_min_length, login_lock_enabled, 
                       operation_log_enabled, login_log_enabled, show_welcome)
VALUES ('供应链管理系统', '智能化供应链管理系统', 6, 0, 1, 1, 1); 