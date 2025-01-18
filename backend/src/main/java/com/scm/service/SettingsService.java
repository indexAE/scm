package com.scm.service;

import com.scm.entity.SystemSettings;

public interface SettingsService {
    SystemSettings getSettings();
    boolean updateSettings(SystemSettings settings);
    boolean initSettings();
} 