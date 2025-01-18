package com.scm.service;

import com.scm.entity.PurchaseRequisition;
import java.util.List;

public interface PurchaseRequisitionService {
    // 插入采购申请
    void insert(PurchaseRequisition requisition);
    
    // 更新采购申请
    void update(PurchaseRequisition requisition);
    
    // 删除采购申请
    void deleteById(Long id);
    
    // 根据ID查询
    PurchaseRequisition selectById(Long id);
    
    // 根据编号查询
    PurchaseRequisition selectByCode(String code);
    
    // 查询列表
    List<PurchaseRequisition> selectList(String code, String status, Integer offset, Integer limit);
    
    // 查询总数
    int selectCount(String code, String status);
    
    // 更新状态
    void updateStatus(Long id, String status, String remark);
} 