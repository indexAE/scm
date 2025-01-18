package com.scm.controller;

import com.scm.common.Result;
import com.scm.common.PageData;
import com.scm.entity.SaleOrder;
import com.scm.service.SaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale/orders")
public class SaleOrderController {
    @Autowired
    private SaleOrderService saleOrderService;
    
    @GetMapping
    public Result<PageData<SaleOrder>> list(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(saleOrderService.getList(orderNo, customerName, status, pageNum, pageSize));
    }
    
    @GetMapping("/{id}")
    public Result<SaleOrder> getById(@PathVariable Long id) {
        return Result.success(saleOrderService.getById(id));
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody SaleOrder order) {
        if (saleOrderService.create(order)) {
            return Result.success();
        }
        return Result.error("创建销售订单失败");
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SaleOrder order) {
        order.setId(id);
        if (saleOrderService.update(order)) {
            return Result.success();
        }
        return Result.error("更新销售订单失败");
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (saleOrderService.updateStatus(id, status)) {
            return Result.success();
        }
        return Result.error("更新销售订单状态失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (saleOrderService.delete(id)) {
            return Result.success();
        }
        return Result.error("删除销售订单失败");
    }

    /**
     * 审核销售订单
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            @RequestParam String approveStatus,
            @RequestParam(required = false) String remark) {
        if (saleOrderService.approve(id, approveStatus, remark)) {
            return Result.success();
        }
        return Result.error("审核失败");
    }
} 