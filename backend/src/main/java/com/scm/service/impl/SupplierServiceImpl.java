package com.scm.service.impl;

import com.scm.entity.Supplier;
import com.scm.mapper.SupplierMapper;
import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    
    @Autowired
    private SupplierMapper supplierMapper;
    
    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierMapper.findAll();
    }
    
    @Override
    public Supplier getSupplierById(Long id) {
        return supplierMapper.findById(id);
    }
    
    @Override
    public boolean createSupplier(Supplier supplier) {
        supplier.setCreateTime(LocalDateTime.now());
        supplier.setUpdateTime(LocalDateTime.now());
        supplier.setStatus(true); // 默认启用
        return supplierMapper.insert(supplier) > 0;
    }
    
    @Override
    public boolean updateSupplier(Supplier supplier) {
        supplier.setUpdateTime(LocalDateTime.now());
        return supplierMapper.update(supplier) > 0;
    }
    
    @Override
    public boolean deleteSupplier(Long id) {
        return supplierMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateSupplierStatus(Long id, Boolean status) {
        return supplierMapper.updateStatus(id, status) > 0;
    }
} 