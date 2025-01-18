package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Handler {
    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 