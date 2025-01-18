package com.scm.mapper;

import com.scm.entity.Dealer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DealerMapper {
    // 获取所有经销商
    List<Dealer> findAll();
    
    // 根据ID获取经销商
    Dealer findById(@Param("id") Long id);
    
    // 创建经销商
    int insert(Dealer dealer);
    
    // 更新经销商
    int update(Dealer dealer);
    
    // 删除经销商
    int deleteById(@Param("id") Long id);
    
    // 更新经销商状态
    int updateStatus(@Param("id") Long id, @Param("status") Boolean status);
} 