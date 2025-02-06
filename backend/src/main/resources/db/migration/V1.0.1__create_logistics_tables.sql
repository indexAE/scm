-- 发货单表
CREATE TABLE t_shipment (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    shipment_no VARCHAR(50) NOT NULL COMMENT '发货单号',
    tracking_no VARCHAR(50) COMMENT '物流单号',
    status VARCHAR(20) NOT NULL COMMENT '状态：pending-待发货，shipped-已发货，in_transit-运输中，delivered-已送达，cancelled-已取消',
    shipper VARCHAR(50) NOT NULL COMMENT '发货人',
    shipper_phone VARCHAR(20) NOT NULL COMMENT '发货人电话',
    shipper_address VARCHAR(255) NOT NULL COMMENT '发货地址',
    consignee VARCHAR(50) NOT NULL COMMENT '收货人',
    consignee_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    consignee_address VARCHAR(255) NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shipment_no (shipment_no, deleted),
    KEY idx_tracking_no (tracking_no, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货单表';

-- 物流跟踪表
CREATE TABLE t_shipment_tracking (
    id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    shipment_id BIGINT NOT NULL COMMENT '发货单ID',
    location VARCHAR(100) NOT NULL COMMENT '当前位置',
    description VARCHAR(500) NOT NULL COMMENT '物流描述',
    status VARCHAR(20) NOT NULL COMMENT '状态：shipped-已发货，in_transit-运输中，delivered-已送达，cancelled-已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    KEY idx_shipment_id (shipment_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流跟踪表'; 