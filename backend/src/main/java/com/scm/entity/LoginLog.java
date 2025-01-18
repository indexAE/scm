package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoginLog {
    private Long id;
    private String username;
    private String ip;
    private Boolean status;
    private String browser;
    private String os;
    private LocalDateTime loginTime;
} 