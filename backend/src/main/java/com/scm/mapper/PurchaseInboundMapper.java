package com.scm.mapper;

import com.scm.entity.PurchaseInbound;
import com.scm.entity.PurchaseInboundItem;
import com.scm.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseInboundMapper {
    // 插入采购入库单
    int insert(PurchaseInbound inbound);
    
    // 插入采购入库明细
    int insertItem(PurchaseInboundItem item);
    
    // 批量插入采购入库明细
    int batchInsertItems(@Param("items") List<PurchaseInboundItem> items);
    
    // 更新采购入库单
    int update(PurchaseInbound inbound);
    
    // 更新采购入库明细
    int updateItem(PurchaseInboundItem item);
    
    // 删除采购入库单
    int deleteById(Long id);
    
    // 删除采购入库明细
    int deleteItemsByInboundId(Long inboundId);
    
    // 根据ID查询
    PurchaseInbound selectById(Long id);
    
    // 根据编号查询
    PurchaseInbound selectByCode(String code);
    
    // 查询入库明细
    List<PurchaseInboundItem> selectItemsByInboundId(Long inboundId);
    
    // 查询列表
    List<PurchaseInbound> selectList(@Param("code") String code, 
                                     @Param("orderCode") String orderCode,
                                     @Param("status") String status,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    
    // 查询总数
    int selectCount(@Param("code") String code, 
                   @Param("orderCode") String orderCode,
                   @Param("status") String status);
    
    // 更新状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    // 根据ID查询采购订单
    PurchaseOrder selectOrderById(@Param("orderId") Long orderId);
} 