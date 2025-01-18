package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Customer {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String address;
    private Boolean status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 