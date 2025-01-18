package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductPrice {
    private Long id;
    private Long productId;         // 商品ID
    private String productCode;     // 商品编码
    private String productName;     // 商品名称
    private String categoryName;    // 商品分类
    private String spec;           // 规格
    private String unit;           // 单位
    private BigDecimal retailPrice; // 零售价
    private BigDecimal wholesalePrice; // 批发价
    private BigDecimal memberPrice; // 会员价
    private String remark;          // 调整原因
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
} 