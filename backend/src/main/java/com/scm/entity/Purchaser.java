package com.scm.entity;

import lombok.Data;

@Data
public class Purchaser {
    private Long id;
    private String name;        // 姓名
    private String phone;       // 手机号
    private String email;       // 邮箱
    private String department;  // 部门
    private String status;      // 状态: enabled-启用, disabled-禁用
    private String createTime;  // 创建时间
    private String updateTime;  // 更新时间
} 