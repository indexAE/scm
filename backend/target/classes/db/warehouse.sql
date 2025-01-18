-- 仓库信息表
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
    code VARCHAR(50) NOT NULL COMMENT '仓库编号',
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    address VARCHAR(200) COMMENT '仓库地址',
    manager VARCHAR(50) COMMENT '仓库管理员',
    contact VARCHAR(20) COMMENT '联系电话',
    status VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '状态：normal-正常，disabled-停用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) COMMENT '仓库信息表';

-- 库存信息表
CREATE TABLE warehouse_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    min_stock INT NOT NULL DEFAULT 0 COMMENT '最小库存',
    max_stock INT NOT NULL DEFAULT 0 COMMENT '最大库存',
    status VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '状态：normal-正常，overstock-积压，shortage-短缺',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_warehouse_product (warehouse_id, product_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) COMMENT '库存信息表';

-- 库存操作记录表
CREATE TABLE warehouse_stock_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：in-入库，out-出库，adjust-调整',
    quantity INT NOT NULL COMMENT '操作数量',
    before_quantity INT NOT NULL COMMENT '操作前数量',
    after_quantity INT NOT NULL COMMENT '操作后数量',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(50) NOT NULL COMMENT '操作人姓名',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    remark VARCHAR(500) COMMENT '备注',
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (operator_id) REFERENCES user(id)
) COMMENT '库存操作记录表'; 