package com.scm.service;

import com.scm.entity.Retailer;
import java.util.List;

public interface RetailerService {
    // 获取所有零售商
    List<Retailer> findAll();
    
    // 根据ID获取零售商
    Retailer findById(Long id);
    
    // 创建零售商
    int create(Retailer retailer);
    
    // 更新零售商
    int update(Retailer retailer);
    
    // 删除零售商
    int deleteById(Long id);
    
    // 更新零售商状态
    int updateStatus(Long id, Boolean status);
} 