-- 初始化采购员数据
INSERT INTO purchaser (name, phone, email, status) VALUES
('张三', '13800138001', 'zhangsan@example.com', 'enabled'),
('李四', '13800138002', 'lisi@example.com', 'enabled'),
('王五', '13800138003', 'wangwu@example.com', 'enabled');

-- 初始化经办人数据
INSERT INTO handler (name, phone, email, status) VALUES
('赵六', '13800138004', 'zhaoliu@example.com', 'enabled'),
('钱七', '13800138005', 'qianqi@example.com', 'enabled'),
('孙八', '13800138006', 'sunba@example.com', 'enabled');

-- 初始化商品分类数据
INSERT INTO product_category (name, code, parent_id, level, status) VALUES
('电子产品', 'ELE', NULL, 1, 'enabled'),
('办公用品', 'OFF', NULL, 1, 'enabled'),
('日用品', 'DAILY', NULL, 1, 'enabled');

-- 初始化商品数据
INSERT INTO product (name, code, category_id, spec, unit, status) VALUES
('笔记本电脑', 'PC001', 1, '14寸 8G内存', '台', 'enabled'),
('打印机', 'PC002', 1, '激光打印', '台', 'enabled'),
('办公桌', 'OF001', 2, '1.4米*0.7米', '张', 'enabled'),
('办公椅', 'OF002', 2, '标准办公椅', '把', 'enabled');

-- 初始化仓库数据
INSERT INTO warehouse (name, code, address, manager, phone, status) VALUES
('主仓库', 'WH001', '北京市朝阳区xx路xx号', '张经理', '13900139001', 'enabled'),
('分仓库1', 'WH002', '上海市浦东新区xx路xx号', '李经理', '13900139002', 'enabled'),
('分仓库2', 'WH003', '广州市天河区xx路xx号', '王经理', '13900139003', 'enabled');

-- 初始化商品定价数据
INSERT INTO product_price (product_id, price, start_date, end_date, status) VALUES
(1, 5999.00, '2024-01-01', '2024-12-31', 'enabled'),
(2, 1999.00, '2024-01-01', '2024-12-31', 'enabled'),
(3, 799.00, '2024-01-01', '2024-12-31', 'enabled'),
(4, 299.00, '2024-01-01', '2024-12-31', 'enabled');

-- 初始化库存数据
INSERT INTO warehouse_stock (warehouse_id, product_id, quantity, unit) VALUES
(1, 1, 100, '台'),
(1, 2, 50, '台'),
(1, 3, 200, '张'),
(1, 4, 300, '把'),
(2, 1, 50, '台'),
(2, 2, 30, '台'),
(3, 3, 100, '张'),
(3, 4, 150, '把'); 