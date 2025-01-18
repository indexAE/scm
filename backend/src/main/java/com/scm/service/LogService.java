package com.scm.service;

import com.scm.entity.LoginLog;
import com.scm.entity.OperationLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LogService {
    // 操作日志
    void recordOperationLog(String username, String operation, String method, String params, String ip);
    Map<String, Object> getOperationLogs(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    
    // 登录日志
    void recordLoginLog(String username, String ip, boolean status, String browser, String os);
    Map<String, Object> getLoginLogs(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
} 