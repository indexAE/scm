package com.scm.service;

import com.scm.entity.Supplier;
import java.util.List;

public interface SupplierService {
    // 获取所有供应商
    List<Supplier> getAllSuppliers();
    
    // 根据ID获取供应商
    Supplier getSupplierById(Long id);
    
    // 创建供应商
    boolean createSupplier(Supplier supplier);
    
    // 更新供应商
    boolean updateSupplier(Supplier supplier);
    
    // 删除供应商
    boolean deleteSupplier(Long id);
    
    // 更新供应商状态
    boolean updateSupplierStatus(Long id, Boolean status);
} 