package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Retailer {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String address;
    private String businessType;  // 经营类型
    private String scale;         // 规模
    private Double creditLimit;   // 信用额度
    private Boolean status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 