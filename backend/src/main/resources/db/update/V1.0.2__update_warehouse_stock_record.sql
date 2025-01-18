-- 删除外键约束
ALTER TABLE warehouse_stock_record DROP FOREIGN KEY warehouse_stock_record_ibfk_3;

-- 修改operator_id列为可空
ALTER TABLE warehouse_stock_record MODIFY operator_id BIGINT NULL COMMENT '操作人ID';
ALTER TABLE warehouse_stock_record MODIFY operator_name VARCHAR(50) NULL COMMENT '操作人姓名'; 