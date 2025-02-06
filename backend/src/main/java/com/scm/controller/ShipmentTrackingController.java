package com.scm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scm.common.Result;
import com.scm.entity.ShipmentTracking;
import com.scm.service.ShipmentTrackingService;
import com.scm.vo.ShipmentTrackingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics/tracking")
public class ShipmentTrackingController {

    @Autowired
    private ShipmentTrackingService trackingService;

    /**
     * 分页查询物流跟踪信息
     */
    @GetMapping("/page")
    public Result<IPage<ShipmentTrackingVO>> getTrackingPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
        IPage<ShipmentTrackingVO> trackingPage = trackingService.getTrackingPage(page, limit, orderNo, status);
        return Result.success(trackingPage);
    }

    /**
     * 获取发货单的物流轨迹
     */
    @GetMapping("/{shipmentId}/timeline")
    public Result<List<ShipmentTracking>> getTrackingList(@PathVariable Long shipmentId) {
        List<ShipmentTracking> trackingList = trackingService.getTrackingList(shipmentId);
        return Result.success(trackingList);
    }

    /**
     * 添加物流跟踪信息
     */
    @PostMapping
    public Result<Void> addTracking(@RequestBody ShipmentTracking tracking) {
        trackingService.addTracking(tracking);
        return Result.success();
    }
} 