package com.scm.mapper;

import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface PurchaseOrderMapper {
    // 插入采购订单
    int insert(PurchaseOrder order);
    
    // 插入采购订单明细
    int insertItem(PurchaseOrderItem item);
    
    // 批量插入采购订单明细
    int batchInsertItems(@Param("items") List<PurchaseOrderItem> items);
    
    // 更新采购订单
    int update(PurchaseOrder order);
    
    // 更新采购订单明细
    int updateItem(PurchaseOrderItem item);
    
    // 删除采购订单
    int deleteById(Long id);
    
    // 删除采购订单明细
    int deleteItemsByOrderId(Long orderId);
    
    // 根据ID查询
    PurchaseOrder selectById(Long id);
    
    // 根据编号查询
    PurchaseOrder selectByCode(String code);
    
    // 查询订单明细
    List<PurchaseOrderItem> selectItemsByOrderId(Long orderId);
    
    // 查询列表
    List<PurchaseOrder> selectList(@Param("code") String code, 
                                   @Param("supplier") String supplier,
                                   @Param("status") String status,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    
    // 查询总数
    int selectCount(@Param("code") String code, 
                   @Param("supplier") String supplier,
                   @Param("status") String status);
    
    // 按日期范围统计订单数量
    int selectCountByDateRange(@Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);
    
    // 更新状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 获取可入库的采购订单列表
    List<PurchaseOrder> selectAvailableForInbound();
}
 
    // 查询列表
 