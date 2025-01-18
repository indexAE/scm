package com.scm.entity;

import lombok.Data;

@Data
public class SystemSettings {
    private Long id;
    private String systemName;
    private String systemDesc;
    private Integer passwordMinLength;
    private Boolean loginLockEnabled;
    private Boolean operationLogEnabled;
    private Boolean loginLogEnabled;
    private Boolean showWelcome;
    private String createTime;
    private String updateTime;
} 