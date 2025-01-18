package com.scm.service;

import java.util.Map;

public interface DashboardService {
    
    /**
     * 获取总体统计数据
     */
    Map<String, Object> getStats();

    /**
     * 获取趋势统计数据
     */
    Map<String, Object> getTrendStats();
} 