package com.scm.service;

import com.scm.common.PageData;
import com.scm.entity.SaleVariance;

public interface SaleVarianceService {
    PageData<SaleVariance> getList(String varianceNo, String customerName, String type, String status, int offset, int limit);
    
    SaleVariance getById(Long id);
    
    void create(SaleVariance variance);
    
    void update(SaleVariance variance);
    
    void updateStatus(Long id, String status);
} 