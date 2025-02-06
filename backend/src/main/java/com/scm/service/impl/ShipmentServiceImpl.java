package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.entity.Shipment;
import com.scm.mapper.ShipmentMapper;
import com.scm.service.ShipmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ShipmentServiceImpl extends ServiceImpl<ShipmentMapper, Shipment> implements ShipmentService {

    @Override
    public IPage<Shipment> getShipmentPage(Integer page, Integer limit, String orderNo, String status) {
        Page<Shipment> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<Shipment> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(orderNo)) {
            queryWrapper.like(Shipment::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Shipment::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Shipment::getCreateTime);
        return baseMapper.selectPage(pageParam, queryWrapper);
    }
} 