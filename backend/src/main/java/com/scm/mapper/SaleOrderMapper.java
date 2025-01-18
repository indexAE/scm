package com.scm.mapper;

import com.scm.entity.SaleOrder;
import com.scm.entity.SaleOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SaleOrderMapper {
    List<SaleOrder> selectList(@Param("orderNo") String orderNo,
                              @Param("customerName") String customerName,
                              @Param("status") String status,
                              @Param("offset") int offset,
                              @Param("limit") int limit);
                              
    int selectCount(@Param("orderNo") String orderNo,
                    @Param("customerName") String customerName,
                    @Param("status") String status);
                    
    int selectCountByDateRange(@Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);
                    
    SaleOrder selectById(@Param("id") Long id);
    
    String selectMaxOrderNoByPrefix(@Param("prefix") String prefix);
    
    int insert(SaleOrder order);
    
    int insertItem(SaleOrderItem item);
    
    int update(SaleOrder order);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    int deleteItems(@Param("orderId") Long orderId);
    
    int delete(@Param("id") Long id);
} 