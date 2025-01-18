-- 添加最小库存字段到商品表
ALTER TABLE product
ADD COLUMN min_stock INT DEFAULT 10 COMMENT '最小库存';

-- 创建库存表
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    current_stock INT NOT NULL DEFAULT 0 COMMENT '当前库存',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    KEY idx_product (product_id),
    KEY idx_warehouse (warehouse_id),
    UNIQUE KEY uk_product_warehouse (product_id, warehouse_id)
) COMMENT '库存表';

-- 添加一些测试数据
INSERT INTO inventory (product_id, warehouse_id, current_stock)
SELECT id, 1, FLOOR(RAND() * 50) 
FROM product; 