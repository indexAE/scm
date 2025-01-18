package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrder {
    private Long id;
    private String code;
    private Long requisitionId;
    private String requisitionCode;
    private Long supplierId;
    private String supplier;
    private BigDecimal amount;
    private Long purchaserId;
    private String purchaser;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 订单明细
    private List<PurchaseOrderItem> items;
} 