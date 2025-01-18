package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ip;
    private LocalDateTime operationTime;
} 