package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderItem {
    private Long id;
    private Long orderId;
    private String orderCode;
    private Long productId;
    private String productName;
    private String productSpec;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 