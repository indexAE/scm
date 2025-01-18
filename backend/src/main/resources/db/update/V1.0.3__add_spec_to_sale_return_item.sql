-- 检查字段是否存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = "sale_return_item";
SET @columnname = "spec";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  "SELECT 1",
  "ALTER TABLE sale_return_item ADD COLUMN spec VARCHAR(200) COMMENT '商品规格' AFTER product_name"
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists; 