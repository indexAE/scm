package com.scm.mapper;

import com.scm.entity.Purchaser;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PurchaserMapper {
    // 查询采购员列表
    List<Purchaser> selectList(@Param("name") String name, 
                             @Param("phone") String phone,
                             @Param("status") String status,
                             @Param("offset") Integer offset,
                             @Param("limit") Integer limit);
    
    // 查询总数
    int selectCount(@Param("name") String name,
                   @Param("phone") String phone,
                   @Param("status") String status);
    
    // 根据ID查询
    Purchaser selectById(@Param("id") Long id);
    
    // 新增采购员
    int insert(Purchaser purchaser);
    
    // 更新采购员
    int update(Purchaser purchaser);
    
    // 更新状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 删除采购员
    int deleteById(@Param("id") Long id);
} 