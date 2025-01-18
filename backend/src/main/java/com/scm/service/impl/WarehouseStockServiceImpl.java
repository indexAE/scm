package com.scm.service.impl;

import com.scm.entity.WarehouseStock;
import com.scm.service.WarehouseStockService;
import com.scm.mapper.WarehouseStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseStockServiceImpl implements WarehouseStockService {

    @Autowired
    private WarehouseStockMapper warehouseStockMapper;

    @Override
    public List<WarehouseStock> selectList(Long warehouseId, String productName, String stockStatus, Integer offset, Integer limit) {
        return warehouseStockMapper.selectList(warehouseId, productName, stockStatus, offset, limit);
    }

    @Override
    public int selectCount(Long warehouseId, String productName, String stockStatus) {
        return warehouseStockMapper.selectCount(warehouseId, productName, stockStatus);
    }

    @Override
    @Transactional
    public void adjustStock(Long id, Integer quantity) {
        // 获取当前库存
        WarehouseStock stock = warehouseStockMapper.selectById(id);
        if (stock == null) {
            throw new RuntimeException("库存记录不存在");
        }

        // 调整库存
        stock.setQuantity(stock.getQuantity().add(new BigDecimal(quantity)));
        if (stock.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("库存不足");
        }

        // 更新库存状态
        if (stock.getQuantity().compareTo(stock.getMinStock()) < 0) {
            stock.setStockStatus("shortage");
        } else if (stock.getQuantity().compareTo(stock.getMaxStock()) > 0) {
            stock.setStockStatus("overstock");
        } else {
            stock.setStockStatus("normal");
        }

        // 更新库存
        warehouseStockMapper.update(stock);

        // 记录库存变动
        warehouseStockMapper.insertRecord(
            id,                    // stockId
            quantity,             // quantity
            stock.getQuantity(),  // afterQuantity
            null,                // operatorId
            null                 // operatorName
        );
    }

    @Override
    public List<Map<String, Object>> selectRecords(Long stockId, Integer offset, Integer limit) {
        return warehouseStockMapper.selectRecords(stockId, offset, limit);
    }

    @Override
    public int selectRecordsCount(Long stockId) {
        return warehouseStockMapper.selectRecordsCount(stockId);
    }
} 