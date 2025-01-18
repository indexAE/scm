-- 采购申请表
CREATE TABLE purchase_requisition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL COMMENT '申请编号',
    dealer_id BIGINT NOT NULL COMMENT '经销商ID',
    dealer VARCHAR(128) NOT NULL COMMENT '经销商',
    amount DECIMAL(10,2) NOT NULL COMMENT '申请金额',
    status VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待审核，approved-已审核，rejected-已驳回',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) COMMENT '采购申请表';

-- 采购申请明细表
CREATE TABLE purchase_requisition_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requisition_id BIGINT NOT NULL COMMENT '采购申请ID',
    requisition_code VARCHAR(32) NOT NULL COMMENT '采购申请编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    product_spec VARCHAR(128) COMMENT '商品规格',
    unit VARCHAR(16) NOT NULL COMMENT '单位',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '采购申请明细表';

-- 采购订单表
CREATE TABLE purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL COMMENT '订单编号',
    requisition_id BIGINT COMMENT '关联的采购申请ID',
    requisition_code VARCHAR(32) COMMENT '关联的采购申请编号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier VARCHAR(128) NOT NULL COMMENT '供应商',
    amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    purchaser_id BIGINT NOT NULL COMMENT '采购员ID',
    purchaser VARCHAR(64) NOT NULL COMMENT '采购员',
    expected_delivery_date DATE COMMENT '预计交付日期',
    actual_delivery_date DATE COMMENT '实际交付日期',
    status VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待确认，confirmed-已确认，completed-已完成，cancelled-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) COMMENT '采购订单表';

-- 采购订单明细表
CREATE TABLE purchase_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '采购订单ID',
    order_code VARCHAR(32) NOT NULL COMMENT '采购订单编号',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    product_spec VARCHAR(128) COMMENT '商品规格',
    unit VARCHAR(16) NOT NULL COMMENT '单位',
    quantity DECIMAL(10,2) NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '采购订单明细表';

-- 采购入库表
CREATE TABLE purchase_inbound (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL COMMENT '入库单号',
    order_id BIGINT NOT NULL COMMENT '采购订单ID',
    order_code VARCHAR(32) NOT NULL COMMENT '采购订单编号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    supplier VARCHAR(128) NOT NULL COMMENT '供应商',
    warehouse_id BIGINT NOT NULL COMMENT '入库仓库ID',
    warehouse VARCHAR(64) NOT NULL COMMENT '入库仓库',
    handler_id BIGINT NOT NULL COMMENT '经办人ID',
    handler VARCHAR(64) NOT NULL COMMENT '经办人',
    status VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待入库，completed-已入库，cancelled-已取消',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) COMMENT '采购入库表';

-- 采购入库明细表
CREATE TABLE purchase_inbound_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inbound_id BIGINT NOT NULL COMMENT '入库单ID',
    inbound_code VARCHAR(32) NOT NULL COMMENT '入库单号',
    order_item_id BIGINT NOT NULL COMMENT '采购订单明细ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    product_spec VARCHAR(128) COMMENT '商品规格',
    unit VARCHAR(16) NOT NULL COMMENT '单位',
    quantity DECIMAL(10,2) NOT NULL COMMENT '数量',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '采购入库明细表'; 