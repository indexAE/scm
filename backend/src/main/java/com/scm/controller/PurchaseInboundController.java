package com.scm.controller;

import com.scm.common.BaseController;
import com.scm.common.Result;
import com.scm.entity.PurchaseInbound;
import com.scm.service.PurchaseInboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase/inbounds")
public class PurchaseInboundController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseInboundController.class);

    @Autowired
    private PurchaseInboundService purchaseInboundService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<PurchaseInbound> list = purchaseInboundService.selectList(code, orderCode, status, offset, limit);
            int total = purchaseInboundService.selectCount(code, orderCode, status);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取入库单列表失败", e);
            return Result.error(500, "获取入库单列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<PurchaseInbound> getById(@PathVariable Long id) {
        try {
            PurchaseInbound inbound = purchaseInboundService.selectById(id);
            if (inbound == null) {
                return Result.error(404, "入库单不存在");
            }
            return Result.success(inbound);
        } catch (Exception e) {
            logger.error("获取入库单详情失败: id={}", id, e);
            return Result.error(500, "获取入库单详情失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result<PurchaseInbound> add(@RequestBody PurchaseInbound inbound) {
        try {
            logger.info("开始创建入库单: {}", inbound);
            purchaseInboundService.insert(inbound);
            logger.info("入库单创建成功: id={}, code={}", inbound.getId(), inbound.getCode());
            return Result.success(inbound);
        } catch (Exception e) {
            logger.error("创建入库单失败", e);
            return Result.error(500, "创建入库单失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<PurchaseInbound> update(@PathVariable Long id, @RequestBody PurchaseInbound inbound) {
        try {
            inbound.setId(id);
            purchaseInboundService.update(inbound);
            return Result.success(inbound);
        } catch (Exception e) {
            logger.error("更新入库单失败: id={}", id, e);
            return Result.error(500, "更新入库单失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            purchaseInboundService.updateStatus(id, status);
            return Result.success();
        } catch (Exception e) {
            logger.error("更新入库单状态失败: id={}, status={}", id, body.get("status"), e);
            return Result.error(500, "更新入库单状态失败: " + e.getMessage());
        }
    }
} 