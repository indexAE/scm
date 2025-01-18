package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseInbound {
    private Long id;
    private String code;
    private Long orderId;
    private String orderCode;
    private Long supplierId;
    private String supplier;
    private Long warehouseId;
    private String warehouse;
    private Long handlerId;
    private String handler;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 入库明细
    private List<PurchaseInboundItem> items;
} 