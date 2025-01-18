package com.scm.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 