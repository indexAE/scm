package com.scm.controller;

import com.scm.common.Result;
import com.scm.common.PageData;
import com.scm.entity.SaleReturn;
import com.scm.service.SaleReturnService;
import com.scm.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale/returns")
public class SaleReturnController extends BaseController {
    
    @Autowired
    private SaleReturnService saleReturnService;
    
    @GetMapping
    public Result<PageData<SaleReturn>> list(
            @RequestParam(required = false) String returnNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        try {
            // Convert offset/limit to pageNum/pageSize
            int pageNum = offset / limit + 1;
            int pageSize = limit;
            logger.info("查询退货单列表: returnNo={}, customerName={}, status={}, pageNum={}, pageSize={}", 
                returnNo, customerName, status, pageNum, pageSize);
            PageData<SaleReturn> pageData = saleReturnService.getList(returnNo, customerName, status, pageNum, pageSize);
            return Result.success(pageData);
        } catch (Exception e) {
            logger.error("查询退货单列表失败", e);
            return Result.error("查询退货单列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<SaleReturn> getById(@PathVariable Long id) {
        try {
            logger.info("查询退货单详情: id={}", id);
            SaleReturn saleReturn = saleReturnService.getById(id);
            return Result.success(saleReturn);
        } catch (Exception e) {
            logger.error("查询退货单详情失败: id={}", id, e);
            return Result.error("查询退货单详情失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Void> create(@RequestBody SaleReturn saleReturn) {
        try {
            logger.info("创建退货单: {}", saleReturn);
            if (saleReturnService.create(saleReturn)) {
                logger.info("创建退货单成功: returnNo={}", saleReturn.getReturnNo());
                return Result.success();
            }
            logger.error("创建退货单失败: {}", saleReturn);
            return Result.error("创建退货单失败");
        } catch (Exception e) {
            logger.error("创建退货单异常", e);
            return Result.error("创建退货单失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SaleReturn saleReturn) {
        try {
            saleReturn.setId(id);
            logger.info("更新退货单: {}", saleReturn);
            if (saleReturnService.update(saleReturn)) {
                logger.info("更新退货单成功: id={}", id);
                return Result.success();
            }
            logger.error("更新退货单失败: id={}", id);
            return Result.error("更新退货单失败");
        } catch (Exception e) {
            logger.error("更新退货单异常: id={}", id, e);
            return Result.error("更新退货单失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            logger.info("更新退货单状态: id={}, status={}", id, status);
            if (saleReturnService.updateStatus(id, status)) {
                logger.info("更新退货单状态成功: id={}, status={}", id, status);
                return Result.success();
            }
            logger.error("更新退货单状态失败: id={}, status={}", id, status);
            return Result.error("更新退货单状态失败");
        } catch (Exception e) {
            logger.error("更新退货单状态异常: id={}, status={}", id, status, e);
            return Result.error("更新退货单状态失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            logger.info("删除退货单: id={}", id);
            if (saleReturnService.delete(id)) {
                logger.info("删除退货单成功: id={}", id);
                return Result.success();
            }
            logger.error("删除退货单失败: id={}", id);
            return Result.error("删除退货单失败");
        } catch (Exception e) {
            logger.error("删除退货单异常: id={}", id, e);
            return Result.error("删除退货单失败: " + e.getMessage());
        }
    }
} 