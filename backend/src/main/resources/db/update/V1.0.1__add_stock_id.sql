-- 添加stock_id字段
ALTER TABLE warehouse_stock_record
ADD COLUMN stock_id BIGINT NOT NULL COMMENT '库存ID' AFTER id;

-- 添加外键约束
ALTER TABLE warehouse_stock_record
ADD CONSTRAINT fk_stock_id FOREIGN KEY (stock_id) REFERENCES warehouse_stock(id);

-- 更新已有记录的stock_id
UPDATE warehouse_stock_record r
SET r.stock_id = (
    SELECT s.id 
    FROM warehouse_stock s 
    WHERE s.warehouse_id = r.warehouse_id 
    AND s.product_id = r.product_id
); 