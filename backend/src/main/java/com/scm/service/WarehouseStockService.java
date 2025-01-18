package com.scm.service;

import com.scm.entity.WarehouseStock;
import java.util.List;
import java.util.Map;

public interface WarehouseStockService {
    
    List<WarehouseStock> selectList(Long warehouseId, String productName, String stockStatus, Integer offset, Integer limit);
    
    int selectCount(Long warehouseId, String productName, String stockStatus);
    
    void adjustStock(Long id, Integer quantity);
    
    List<Map<String, Object>> selectRecords(Long stockId, Integer offset, Integer limit);
    
    int selectRecordsCount(Long stockId);
} 