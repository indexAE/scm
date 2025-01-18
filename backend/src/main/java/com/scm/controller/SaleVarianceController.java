package com.scm.controller;

import com.scm.common.Result;
import com.scm.common.PageData;
import com.scm.entity.SaleVariance;
import com.scm.service.SaleVarianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale/variances")
public class SaleVarianceController {
    
    @Autowired
    private SaleVarianceService saleVarianceService;
    
    @GetMapping
    public Result<PageData<SaleVariance>> getList(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String varianceNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        try {
            PageData<SaleVariance> pageData = saleVarianceService.getList(
                varianceNo, customerName, type, status, offset, limit);
            return Result.success(pageData);
        } catch (Exception e) {
            return Result.error("获取销售差异列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<SaleVariance> getById(@PathVariable Long id) {
        try {
            SaleVariance variance = saleVarianceService.getById(id);
            return Result.success(variance);
        } catch (Exception e) {
            return Result.error("获取销售差异详情失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody SaleVariance variance) {
        try {
            saleVarianceService.create(variance);
            return Result.success();
        } catch (Exception e) {
            return Result.error("创建销售差异失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SaleVariance variance) {
        try {
            variance.setId(id);
            saleVarianceService.update(variance);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新销售差异失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            saleVarianceService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新销售差异状态失败：" + e.getMessage());
        }
    }
} 