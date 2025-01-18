package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String code;        // 商品编码
    private String name;        // 商品名称
    private Long categoryId;    // 分类ID
    private String categoryName; // 分类名称
    private String spec;        // 规格
    private String unit;        // 单位
    private BigDecimal price;   // 价格
    private Integer stock;      // 库存
    private String status;      // 状态：enabled-启用，disabled-禁用
    private String remark;      // 备注
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
} 