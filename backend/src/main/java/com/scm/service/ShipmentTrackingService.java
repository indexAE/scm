package com.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.entity.ShipmentTracking;
import com.scm.vo.ShipmentTrackingVO;

import java.util.List;

public interface ShipmentTrackingService extends IService<ShipmentTracking> {
    
    /**
     * 分页查询物流跟踪信息
     */
    IPage<ShipmentTrackingVO> getTrackingPage(Integer page, Integer limit, String orderNo, String status);
    
    /**
     * 获取发货单的物流轨迹
     */
    List<ShipmentTracking> getTrackingList(Long shipmentId);
    
    /**
     * 添加物流跟踪信息
     */
    void addTracking(ShipmentTracking tracking);
} 