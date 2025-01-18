package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseInboundItem {
    private Long id;
    private Long inboundId;
    private String inboundCode;
    private Long orderItemId;
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