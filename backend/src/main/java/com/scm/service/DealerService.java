package com.scm.service;

import com.scm.entity.Dealer;
import java.util.List;

public interface DealerService {
    // 获取所有经销商
    List<Dealer> findAll();
    
    // 根据ID获取经销商
    Dealer findById(Long id);
    
    // 创建经销商
    int create(Dealer dealer);
    
    // 更新经销商
    int update(Dealer dealer);
    
    // 删除经销商
    int deleteById(Long id);
    
    // 更新经销商状态
    int updateStatus(Long id, Boolean status);
} 