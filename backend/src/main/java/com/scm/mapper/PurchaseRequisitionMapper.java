package com.scm.mapper;

import com.scm.entity.PurchaseRequisition;
import com.scm.entity.PurchaseRequisitionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseRequisitionMapper {
    // 插入采购申请
    int insert(PurchaseRequisition requisition);
    
    // 更新采购申请
    int update(PurchaseRequisition requisition);
    
    // 删除采购申请
    int deleteById(Long id);
    
    // 根据ID查询
    PurchaseRequisition selectById(Long id);
    
    // 根据编号查询
    PurchaseRequisition selectByCode(String code);
    
    // 查询列表
    List<PurchaseRequisition> selectList(@Param("code") String code, 
                                          @Param("status") String status,
                                          @Param("offset") Integer offset,
                                          @Param("limit") Integer limit);
    
    // 查询总数
    int selectCount(@Param("code") String code, @Param("status") String status);
    
    // 更新状态
    void updateStatus(PurchaseRequisition requisition);
    
    // 批量插入采购申请明细
    int batchInsertItems(@Param("items") List<PurchaseRequisitionItem> items);
} 