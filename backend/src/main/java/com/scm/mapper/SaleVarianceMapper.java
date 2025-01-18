package com.scm.mapper;

import com.scm.entity.SaleVariance;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SaleVarianceMapper {
    List<SaleVariance> selectList(
        @Param("varianceNo") String varianceNo,
        @Param("customerName") String customerName,
        @Param("type") String type,
        @Param("status") String status,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    int selectCount(
        @Param("varianceNo") String varianceNo,
        @Param("customerName") String customerName,
        @Param("type") String type,
        @Param("status") String status
    );
    
    SaleVariance selectById(@Param("id") Long id);
    
    void insert(SaleVariance variance);
    
    void update(SaleVariance variance);
    
    void updateStatus(@Param("id") Long id, @Param("status") String status);
} 