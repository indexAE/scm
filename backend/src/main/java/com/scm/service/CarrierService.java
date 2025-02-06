package com.scm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scm.entity.Carrier;

public interface CarrierService extends IService<Carrier> {
    
    /**
     * 分页查询承运商列表
     */
    IPage<Carrier> getCarrierPage(Integer page, Integer limit, String name, String contact, String status);
    
    /**
     * 启用承运商
     */
    void enableCarrier(Long id);
    
    /**
     * 禁用承运商
     */
    void disableCarrier(Long id);
} 