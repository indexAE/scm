package com.scm.mapper;

import com.scm.entity.WarehouseStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WarehouseStockMapper {
    
    List<WarehouseStock> selectList(@Param("warehouseId") Long warehouseId,
                                   @Param("productName") String productName,
                                   @Param("stockStatus") String stockStatus,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    
    int selectCount(@Param("warehouseId") Long warehouseId,
                   @Param("productName") String productName,
                   @Param("stockStatus") String stockStatus);
    
    WarehouseStock selectById(@Param("id") Long id);
    
    void update(WarehouseStock stock);
    
    void insertRecord(@Param("stockId") Long stockId,
                     @Param("quantity") Integer quantity,
                     @Param("afterQuantity") BigDecimal afterQuantity,
                     @Param("operatorId") Long operatorId,
                     @Param("operatorName") String operatorName);
    
    @Select("SELECT r.*, u.username as operator_name " +
            "FROM warehouse_stock_record r " +
            "LEFT JOIN user u ON r.operator_id = u.id " +
            "WHERE r.stock_id = #{stockId} " +
            "ORDER BY r.operation_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectRecords(@Param("stockId") Long stockId, 
                                          @Param("offset") Integer offset, 
                                          @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(1) FROM warehouse_stock_record WHERE stock_id = #{stockId}")
    int selectRecordsCount(@Param("stockId") Long stockId);
} 