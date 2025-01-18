package com.scm.controller;

import com.scm.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    
    @Autowired
    private LogService logService;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @GetMapping("/operation")
    public Map<String, Object> getOperationLogs(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            
            Map<String, Object> data = logService.getOperationLogs(start, end, page, size);
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/login")
    public Map<String, Object> getLoginLogs(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
            
            Map<String, Object> data = logService.getLoginLogs(start, end, page, size);
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
} 