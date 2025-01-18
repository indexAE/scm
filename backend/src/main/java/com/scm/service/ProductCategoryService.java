package com.scm.service;

import com.scm.entity.ProductCategory;
import java.util.List;

public interface ProductCategoryService {
    
    ProductCategory getById(Long id);
    
    List<ProductCategory> search(String name, String status);
    
    List<ProductCategory> getTree();
    
    void create(ProductCategory category);
    
    void update(ProductCategory category);
    
    void updateStatus(Long id, String status);
    
    void checkCode(String code, Long excludeId);
    
    List<ProductCategory> getChildren(Long parentId);
} 