package com.scm.mapper;

import com.scm.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    
    Product getById(@Param("id") Long id);
    
    List<Product> search(
        @Param("name") String name,
        @Param("code") String code,
        @Param("categoryId") Long categoryId,
        @Param("status") String status,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    long count(
        @Param("name") String name,
        @Param("code") String code,
        @Param("categoryId") Long categoryId,
        @Param("status") String status
    );
    
    int insert(Product product);
    
    int update(Product product);
    
    int updateStatus(
        @Param("id") Long id,
        @Param("status") String status,
        @Param("updateBy") Long updateBy
    );
    
    Product getByCode(
        @Param("code") String code,
        @Param("excludeId") Long excludeId
    );
} 