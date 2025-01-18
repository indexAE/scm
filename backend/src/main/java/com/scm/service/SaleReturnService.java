package com.scm.service;

import com.scm.entity.SaleReturn;
import com.scm.common.PageData;

public interface SaleReturnService {
    PageData<SaleReturn> getList(String returnNo, String customerName, String status, int pageNum, int pageSize);
    
    SaleReturn getById(Long id);
    
    boolean create(SaleReturn saleReturn);
    
    boolean update(SaleReturn saleReturn);
    
    boolean updateStatus(Long id, String status);
    
    boolean delete(Long id);
} 