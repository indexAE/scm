package com.scm.service;

import com.scm.entity.Purchaser;
import java.util.List;

public interface PurchaserService {
    // 查询采购员列表
    List<Purchaser> selectList(String name, String phone, String status, Integer offset, Integer limit);
    
    // 查询总数
    int selectCount(String name, String phone, String status);
    
    // 根据ID查询
    Purchaser selectById(Long id);
    
    // 新增采购员
    void insert(Purchaser purchaser);
    
    // 更新采购员
    void update(Purchaser purchaser);
    
    // 更新状态
    void updateStatus(Long id, String status);
    
    // 删除采购员
    void deleteById(Long id);
} 