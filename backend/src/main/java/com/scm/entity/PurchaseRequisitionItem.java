package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PurchaseRequisitionItem {
    private Long id;
    private Long requisitionId;
    private String requisitionCode;
    private Long productId;
    private String productName;
    private String productSpec;
    private String unit;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String remark;
    private Date createTime;
    private Date updateTime;
} 