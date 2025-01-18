package com.scm.service;

import com.scm.entity.PurchaseInbound;
import java.util.List;

public interface PurchaseInboundService {
    List<PurchaseInbound> selectList(String code, String orderCode, String status, Integer offset, Integer limit);
    
    int selectCount(String code, String orderCode, String status);
    
    PurchaseInbound selectById(Long id);
    
    void insert(PurchaseInbound inbound);
    
    void update(PurchaseInbound inbound);
    
    void updateStatus(Long id, String status);
} 