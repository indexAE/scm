-- 发货表
DROP TABLE IF EXISTS `t_shipment`;
CREATE TABLE `t_shipment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `consignee` varchar(50) NOT NULL COMMENT '收货人',
  `consignee_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `consignee_address` varchar(255) NOT NULL COMMENT '收货地址',
  `shipper` varchar(50) NOT NULL COMMENT '发货人',
  `shipper_phone` varchar(20) NOT NULL COMMENT '发货人电话',
  `shipper_address` varchar(255) NOT NULL COMMENT '发货地址',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发货表'; 