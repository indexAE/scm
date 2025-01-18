package com.scm.controller;

import com.scm.common.BaseController;
import com.scm.common.Result;
import com.scm.entity.WarehouseStock;
import com.scm.service.WarehouseStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/stocks")
public class WarehouseStockController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(WarehouseStockController.class);
    
    @Autowired
    private WarehouseStockService warehouseStockService;
    
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<WarehouseStock> list = warehouseStockService.selectList(warehouseId, productName, stockStatus, offset, limit);
            int total = warehouseStockService.selectCount(warehouseId, productName, stockStatus);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取库存列表失败", e);
            return Result.error(500, "获取库存列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{id}/adjust")
    public Result<Void> adjust(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer quantity = body.get("quantity");
            if (quantity == null) {
                return Result.error(400, "调整数量不能为空");
            }
            
            warehouseStockService.adjustStock(id, quantity);
            return Result.success();
        } catch (Exception e) {
            logger.error("库存调整失败: id={}, quantity={}", id, body.get("quantity"), e);
            return Result.error(500, "库存调整失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/records")
    public Result<Map<String, Object>> records(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> list = warehouseStockService.selectRecords(id, offset, limit);
            int total = warehouseStockService.selectRecordsCount(id);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取库存明细记录失败: id={}", id, e);
            return Result.error(500, "获取库存明细记录失败: " + e.getMessage());
        }
    }
} 