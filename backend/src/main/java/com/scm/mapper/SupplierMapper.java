package com.scm.mapper;

import com.scm.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SupplierMapper {
    // 获取所有供应商
    List<Supplier> findAll();
    
    // 根据ID获取供应商
    Supplier findById(@Param("id") Long id);
    
    // 创建供应商
    int insert(Supplier supplier);
    
    // 更新供应商
    int update(Supplier supplier);
    
    // 删除供应商
    int deleteById(@Param("id") Long id);
    
    // 更新供应商状态
    int updateStatus(@Param("id") Long id, @Param("status") Boolean status);
} 