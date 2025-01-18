package com.scm.service;

import com.scm.entity.Product;
import com.scm.common.api.PageResult;
import java.util.List;

public interface ProductService {
    
    Product getById(Long id);
    
    PageResult<Product> search(String name, String code, Long categoryId, String status, int pageNum, int pageSize);
    
    void create(Product product);
    
    void update(Product product);
    
    void updateStatus(Long id, String status);
    
    void checkCode(String code, Long excludeId);
} 