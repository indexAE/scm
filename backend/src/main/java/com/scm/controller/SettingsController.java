package com.scm.controller;

import com.scm.entity.SystemSettings;
import com.scm.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    
    @Autowired
    private SettingsService settingsService;
    
    @GetMapping
    public Map<String, Object> getSettings() {
        Map<String, Object> result = new HashMap<>();
        try {
            SystemSettings settings = settingsService.getSettings();
            result.put("code", 0);
            result.put("data", settings);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取设置失败");
        }
        return result;
    }
    
    @PutMapping
    public Map<String, Object> updateSettings(@RequestBody SystemSettings settings) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = settingsService.updateSettings(settings);
            if (success) {
                result.put("code", 0);
                result.put("message", "更新成功");
            } else {
                result.put("code", 1);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "更新失败");
        }
        return result;
    }
} 