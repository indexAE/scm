CREATE TABLE handler (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(32) NOT NULL COMMENT '编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '状态：normal-正常，disabled-禁用',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    KEY idx_status (status)
) COMMENT '经办人表'; 