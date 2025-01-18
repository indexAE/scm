-- 退货单表
CREATE TABLE sale_return (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '退货单ID',
    return_no VARCHAR(32) NOT NULL COMMENT '退货单号',
    order_id BIGINT NOT NULL COMMENT '关联订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '关联订单号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '退货总金额',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待处理，processing-处理中，completed-已完成，cancelled-已取消',
    remark VARCHAR(500) COMMENT '备注',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_return_no (return_no),
    KEY idx_order_id (order_id),
    KEY idx_customer_id (customer_id),
    KEY idx_status (status)
) COMMENT '退货单表';

-- 退货单明细表
CREATE TABLE sale_return_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '退货单明细ID',
    return_id BIGINT NOT NULL COMMENT '退货单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    spec VARCHAR(200) COMMENT '商品规格',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    quantity DECIMAL(10,2) NOT NULL COMMENT '退货数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总金额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_return_id (return_id),
    KEY idx_product_id (product_id)
) COMMENT '退货单明细表'; 