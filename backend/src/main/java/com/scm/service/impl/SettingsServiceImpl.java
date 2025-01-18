package com.scm.service.impl;

import com.scm.entity.SystemSettings;
import com.scm.mapper.SettingsMapper;
import com.scm.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class SettingsServiceImpl implements SettingsService {
    
    @Autowired
    private SettingsMapper settingsMapper;
    
    @PostConstruct
    public void init() {
        // 应用启动时检查是否存在系统设置
        SystemSettings settings = getSettings();
        if (settings == null) {
            // 不存在则初始化
            initSettings();
        }
    }
    
    @Override
    public SystemSettings getSettings() {
        return settingsMapper.getSettings();
    }
    
    @Override
    public boolean updateSettings(SystemSettings settings) {
        return settingsMapper.updateSettings(settings) > 0;
    }

    @Override
    public boolean initSettings() {
        SystemSettings settings = new SystemSettings();
        settings.setSystemName("供应链管理系统");
        settings.setSystemDesc("智能化供应链管理系统");
        settings.setPasswordMinLength(6);
        settings.setLoginLockEnabled(true);
        settings.setOperationLogEnabled(true);
        settings.setLoginLogEnabled(true);
        settings.setShowWelcome(true);
        
        return settingsMapper.initSettings(settings) > 0;
    }
} 