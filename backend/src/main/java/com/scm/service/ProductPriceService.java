package com.scm.service;

import com.scm.entity.ProductPrice;
import com.scm.common.api.PageResult;
import java.util.List;

public interface ProductPriceService {
    
    ProductPrice getById(Long id);
    
    PageResult<ProductPrice> search(String productName, Long categoryId, int pageNum, int pageSize);
    
    void create(ProductPrice price);
    
    void update(ProductPrice price);
    
    List<ProductPrice> getHistoryByProductId(Long productId);
    
    ProductPrice getLatestByProductId(Long productId);
} 