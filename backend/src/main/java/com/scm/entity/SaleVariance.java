package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SaleVariance {
    private Long id;
    private String varianceNo;
    private Long orderId;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private Integer orderQuantity;    // 订单数量
    private Integer actualQuantity;   // 实际数量
    private Integer varianceQuantity; // 差异数量
    private BigDecimal unitPrice;
    private BigDecimal amount; // 差异金额
    private String type;  // quantity-数量差异, amount-金额差异, other-其他差异
    private String status;  // pending-待处理, processing-处理中, completed-已完成
    private String reason;  // 差异原因
    private String solution; // 处理方案
    private String remark;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 