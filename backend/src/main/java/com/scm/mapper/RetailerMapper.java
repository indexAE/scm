package com.scm.mapper;

import com.scm.entity.Retailer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RetailerMapper {
    // 获取所有零售商
    List<Retailer> findAll();
    
    // 根据ID获取零售商
    Retailer findById(@Param("id") Long id);
    
    // 创建零售商
    int insert(Retailer retailer);
    
    // 更新零售商
    int update(Retailer retailer);
    
    // 删除零售商
    int deleteById(@Param("id") Long id);
    
    // 更新零售商状态
    int updateStatus(@Param("id") Long id, @Param("status") Boolean status);
} 