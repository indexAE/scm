package com.scm.mapper;

import com.scm.entity.SystemSettings;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SettingsMapper {
    
    @Select("SELECT * FROM settings LIMIT 1")
    SystemSettings getSettings();
    
    @Update("UPDATE settings SET system_name = #{systemName}, system_desc = #{systemDesc}, " +
            "password_min_length = #{passwordMinLength}, login_lock_enabled = #{loginLockEnabled}, " +
            "operation_log_enabled = #{operationLogEnabled}, login_log_enabled = #{loginLogEnabled}, " +
            "show_welcome = #{showWelcome} WHERE id = #{id}")
    int updateSettings(SystemSettings settings);

    @Insert("INSERT INTO settings (" +
            "system_name, system_desc, password_min_length, login_lock_enabled, " +
            "operation_log_enabled, login_log_enabled, show_welcome, create_time, update_time" +
            ") VALUES (" +
            "#{systemName}, #{systemDesc}, #{passwordMinLength}, #{loginLockEnabled}, " +
            "#{operationLogEnabled}, #{loginLogEnabled}, #{showWelcome}, NOW(), NOW()" +
            ")")
    int initSettings(SystemSettings settings);
} 