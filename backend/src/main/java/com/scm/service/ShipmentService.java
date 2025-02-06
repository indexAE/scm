package com.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.entity.Shipment;

public interface ShipmentService extends IService<Shipment> {
    
    /**
     * 分页查询发货单列表
     */
    IPage<Shipment> getShipmentPage(Integer page, Integer limit, String orderNo, String status);
} 