package com.scm.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WarehouseStock {
    private Long id;
    private Long warehouseId;
    private String warehouse;
    private Long productId;
    private String productCode;
    private String productName;
    private String productSpec;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal minStock;
    private BigDecimal maxStock;
    private String stockStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 