package com.scm.mapper;

import com.scm.entity.SaleOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SaleOrderItemMapper {
    List<SaleOrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    int batchInsert(@Param("items") List<SaleOrderItem> items);
    
    int deleteByOrderId(@Param("orderId") Long orderId);
} 