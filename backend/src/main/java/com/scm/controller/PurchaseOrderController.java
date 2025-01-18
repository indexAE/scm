package com.scm.controller;

import com.scm.common.BaseController;
import com.scm.common.Result;
import com.scm.entity.PurchaseOrder;
import com.scm.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase/orders")
public class PurchaseOrderController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<PurchaseOrder> list = purchaseOrderService.selectList(code, supplier, status, offset, limit);
        int total = purchaseOrderService.selectCount(code, supplier, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.selectById(id);
        return Result.success(order);
    }

    @PostMapping
    public Result<PurchaseOrder> add(@RequestBody PurchaseOrder order) {
        purchaseOrderService.insert(order);
        return Result.success(order);
    }

    @PutMapping("/{id}")
    public Result<PurchaseOrder> update(@PathVariable Long id, @RequestBody PurchaseOrder order) {
        order.setId(id);
        purchaseOrderService.update(order);
        return Result.success(order);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        purchaseOrderService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        purchaseOrderService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/available-for-inbound")
    public Result<List<PurchaseOrder>> getAvailableForInbound() {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.selectAvailableForInbound();
            logger.info("Available orders for inbound: {}", orders);
            return Result.success(orders);
        } catch (Exception e) {
            logger.error("Error getting available orders for inbound", e);
            return Result.error(500, "获取可入库订单失败: " + e.getMessage());
        }
    }

    // 测试接口 - 获取所有confirmed状态的订单
    @GetMapping("/test/confirmed")
    public Result<List<PurchaseOrder>> getConfirmedOrders() {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.selectList(null, null, "confirmed", 0, 100);
            logger.info("Confirmed orders: {}", orders);
            return Result.success(orders);
        } catch (Exception e) {
            logger.error("Error getting confirmed orders", e);
            return Result.error(500, "获取已确认订单失败: " + e.getMessage());
        }
    }
} 