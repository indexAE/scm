package com.scm.entity;

import lombok.Data;

@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Boolean status;
    private String createTime;
    private String updateTime;
} 