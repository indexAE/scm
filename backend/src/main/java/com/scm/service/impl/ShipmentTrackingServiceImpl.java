package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.entity.Shipment;
import com.scm.entity.ShipmentTracking;
import com.scm.mapper.ShipmentMapper;
import com.scm.mapper.ShipmentTrackingMapper;
import com.scm.service.ShipmentTrackingService;
import com.scm.vo.ShipmentTrackingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShipmentTrackingServiceImpl extends ServiceImpl<ShipmentTrackingMapper, ShipmentTracking> implements ShipmentTrackingService {

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Override
    public IPage<ShipmentTrackingVO> getTrackingPage(Integer page, Integer limit, String orderNo, String status) {
        // 查询发货单
        LambdaQueryWrapper<Shipment> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            queryWrapper.like(Shipment::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Shipment::getStatus, status);
        }
        IPage<Shipment> shipmentPage = shipmentMapper.selectPage(new Page<>(page, limit), queryWrapper);

        // 转换为VO
        List<ShipmentTrackingVO> records = new ArrayList<>();
        for (Shipment shipment : shipmentPage.getRecords()) {
            ShipmentTrackingVO vo = new ShipmentTrackingVO();
            vo.setOrderNo(shipment.getOrderNo());
            vo.setStatus(shipment.getStatus());
            vo.setConsignee(shipment.getConsignee());
            vo.setConsigneePhone(shipment.getConsigneePhone());
            vo.setConsigneeAddress(shipment.getConsigneeAddress());
            vo.setCreateTime(shipment.getCreateTime());

            // 查询最新的物流跟踪信息
            LambdaQueryWrapper<ShipmentTracking> trackingWrapper = new LambdaQueryWrapper<>();
            trackingWrapper.eq(ShipmentTracking::getShipmentId, shipment.getId())
                    .orderByDesc(ShipmentTracking::getCreateTime)
                    .last("LIMIT 1");
            ShipmentTracking tracking = baseMapper.selectOne(trackingWrapper);
            if (tracking != null) {
                vo.setLocation(tracking.getLocation());
                vo.setDescription(tracking.getDescription());
                vo.setTrackingTime(tracking.getCreateTime());
            }
            records.add(vo);
        }

        // 构造分页结果
        IPage<ShipmentTrackingVO> result = new Page<>(page, limit, shipmentPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public List<ShipmentTracking> getTrackingList(Long shipmentId) {
        // 查询指定发货单的所有物流跟踪记录，按创建时间倒序排列
        LambdaQueryWrapper<ShipmentTracking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShipmentTracking::getShipmentId, shipmentId)
                .orderByDesc(ShipmentTracking::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public void addTracking(ShipmentTracking tracking) {
        // 查询发货单是否存在
        Shipment shipment = shipmentMapper.selectById(tracking.getShipmentId());
        if (shipment == null) {
            throw new RuntimeException("发货单不存在");
        }
        save(tracking);
    }
} 