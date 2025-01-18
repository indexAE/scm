package com.scm.service;

import com.scm.entity.PurchaseOrder;
import java.util.List;

public interface PurchaseOrderService {
    List<PurchaseOrder> selectList(String code, String supplier, String status, Integer offset, Integer limit);
    
    int selectCount(String code, String supplier, String status);
    
    PurchaseOrder selectById(Long id);
    
    void insert(PurchaseOrder order);
    
    void update(PurchaseOrder order);
    
    void deleteById(Long id);
    
    void updateStatus(Long id, String status);
    
    // 获取可入库的采购订单列表
    List<PurchaseOrder> selectAvailableForInbound();
} 