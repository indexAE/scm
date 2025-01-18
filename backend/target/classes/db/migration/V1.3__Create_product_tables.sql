-- 商品表
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL COMMENT '商品编码',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    spec VARCHAR(100) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存',
    status VARCHAR(20) NOT NULL DEFAULT 'enabled' COMMENT '状态：enabled-启用，disabled-禁用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    UNIQUE KEY uk_code (code),
    KEY idx_category (category_id),
    KEY idx_status (status)
) COMMENT '商品表';

-- 商品分类表
CREATE TABLE product_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT COMMENT '父分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL COMMENT '分类编码',
    sort INT DEFAULT 0 COMMENT '排序',
    status VARCHAR(20) NOT NULL DEFAULT 'enabled' COMMENT '状态：enabled-启用，disabled-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    UNIQUE KEY uk_code (code),
    KEY idx_parent (parent_id),
    KEY idx_status (status)
) COMMENT '商品分类表';

-- 商品价格表
CREATE TABLE product_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    retail_price DECIMAL(10,2) NOT NULL COMMENT '零售价',
    wholesale_price DECIMAL(10,2) NOT NULL COMMENT '批发价',
    member_price DECIMAL(10,2) NOT NULL COMMENT '会员价',
    remark VARCHAR(500) COMMENT '调整原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    KEY idx_product (product_id)
) COMMENT '商品价格表'; 