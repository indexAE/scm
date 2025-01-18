package com.scm.mapper;

import com.scm.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    ProductCategory getById(Long id);
    
    List<ProductCategory> search(String name, String status);
    
    int insert(ProductCategory category);
    
    int update(ProductCategory category);
    
    int updateStatus(Long id, String status, Long updateBy);
    
    ProductCategory getByCode(String code, Long excludeId);
    
    List<ProductCategory> getByParentId(Long parentId);
} 