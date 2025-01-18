package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseRequisition {
    private Long id;
    private String code;
    private Long dealerId;
    private String dealer;
    private BigDecimal amount;
    private String status;
    private String remark;
    private Date createTime;
    private Date updateTime;
    private List<PurchaseRequisitionItem> items;
} 