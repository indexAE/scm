package com.scm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scm.common.Result;
import com.scm.entity.Shipment;
import com.scm.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/logistics/shipment")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    /**
     * 创建发货单
     */
    @PostMapping
    public Result<?> createShipment(@RequestBody Shipment shipment) {
        try {
            log.info("创建发货单，参数：{}", shipment);
            // 设置初始状态为待发货
            shipment.setStatus("pending");
            shipmentService.save(shipment);
            return Result.success();
        } catch (Exception e) {
            log.error("创建发货单失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新发货单
     */
    @PutMapping("/{id}")
    public Result<?> updateShipment(@PathVariable Long id, @RequestBody Shipment shipment) {
        try {
            log.info("更新发货单，id：{}，参数：{}", id, shipment);
            shipment.setId(id);
            shipmentService.updateById(shipment);
            return Result.success();
        } catch (Exception e) {
            log.error("更新发货单失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取发货单详情
     */
    @GetMapping("/{id}")
    public Result<Shipment> getShipment(@PathVariable Long id) {
        try {
            log.info("获取发货单详情，id：{}", id);
            return Result.success(shipmentService.getById(id));
        } catch (Exception e) {
            log.error("获取发货单详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询发货单列表
     */
    @GetMapping("/page")
    public Result<IPage<Shipment>> getShipmentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
        try {
            log.info("分页查询发货单列表，page：{}，limit：{}，orderNo：{}，status：{}", page, limit, orderNo, status);
            return Result.success(shipmentService.getShipmentPage(page, limit, orderNo, status));
        } catch (Exception e) {
            log.error("分页查询发货单列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消发货单
     */
    @PostMapping("/{id}/cancel")
    public Result<?> cancelShipment(@PathVariable Long id) {
        try {
            log.info("取消发货单，id：{}", id);
            Shipment shipment = new Shipment();
            shipment.setId(id);
            shipment.setStatus("cancelled");
            shipmentService.updateById(shipment);
            return Result.success();
        } catch (Exception e) {
            log.error("取消发货单失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认发货
     */
    @PostMapping("/{id}/ship")
    public Result<?> shipOrder(@PathVariable Long id) {
        try {
            log.info("确认发货，id：{}", id);
            Shipment shipment = new Shipment();
            shipment.setId(id);
            shipment.setStatus("shipped");
            shipmentService.updateById(shipment);
            return Result.success();
        } catch (Exception e) {
            log.error("确认发货失败", e);
            return Result.error(e.getMessage());
        }
    }
} 