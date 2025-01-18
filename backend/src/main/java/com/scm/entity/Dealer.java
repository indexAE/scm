package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Dealer {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String address;
    private String region;      // 经营区域
    private String level;       // 经销商级别
    private Double creditLimit; // 信用额度
    private Boolean status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 