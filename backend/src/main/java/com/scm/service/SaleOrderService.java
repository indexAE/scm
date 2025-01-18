package com.scm.service;

import com.scm.entity.SaleOrder;
import com.scm.common.PageData;

public interface SaleOrderService {
    PageData<SaleOrder> getList(String orderNo, String customerName, String status, int pageNum, int pageSize);
    
    SaleOrder getById(Long id);
    
    boolean create(SaleOrder order);
    
    boolean update(SaleOrder order);
    
    boolean updateStatus(Long id, String status);
    
    boolean delete(Long id);
    
    /**
     * 审核销售订单
     * @param id 订单ID
     * @param approveStatus 审核状态 (approved-通过, rejected-拒绝)
     * @param remark 审核备注
     * @return 是否成功
     */
    boolean approve(Long id, String approveStatus, String remark);
} 