package com.scm.service.impl;

import com.scm.entity.LoginLog;
import com.scm.entity.OperationLog;
import com.scm.entity.SystemSettings;
import com.scm.mapper.LogMapper;
import com.scm.service.LogService;
import com.scm.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {
    
    @Autowired
    private LogMapper logMapper;
    
    @Autowired
    private SettingsService settingsService;
    
    @Override
    public void recordOperationLog(String username, String operation, String method, String params, String ip) {
        // 检查操作日志是否启用
        SystemSettings settings = settingsService.getSettings();
        if (settings == null || !settings.getOperationLogEnabled()) {
            return;
        }
        
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        log.setOperationTime(LocalDateTime.now());
        logMapper.insertOperationLog(log);
    }
    
    @Override
    public Map<String, Object> getOperationLogs(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // 检查操作日志是否启用
        SystemSettings settings = settingsService.getSettings();
        if (settings == null || !settings.getOperationLogEnabled()) {
            return new HashMap<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        int offset = (page - 1) * size;
        
        List<OperationLog> logs = logMapper.getOperationLogs(startDate, endDate, offset, size);
        int total = logMapper.getOperationLogCount(startDate, endDate);
        
        result.put("records", logs);
        result.put("total", total);
        return result;
    }
    
    @Override
    public void recordLoginLog(String username, String ip, boolean status, String browser, String os) {
        // 检查登录日志是否启用
        SystemSettings settings = settingsService.getSettings();
        if (settings == null || !settings.getLoginLogEnabled()) {
            return;
        }
        
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setIp(ip);
        log.setStatus(status);
        log.setBrowser(browser);
        log.setOs(os);
        log.setLoginTime(LocalDateTime.now());
        logMapper.insertLoginLog(log);
    }
    
    @Override
    public Map<String, Object> getLoginLogs(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // 检查登录日志是否启用
        SystemSettings settings = settingsService.getSettings();
        if (settings == null || !settings.getLoginLogEnabled()) {
            return new HashMap<>();
        }
        
        Map<String, Object> result = new HashMap<>();
        int offset = (page - 1) * size;
        
        List<LoginLog> logs = logMapper.getLoginLogs(startDate, endDate, offset, size);
        int total = logMapper.getLoginLogCount(startDate, endDate);
        
        result.put("records", logs);
        result.put("total", total);
        return result;
    }
} 