package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.entity.Carrier;
import com.scm.mapper.CarrierMapper;
import com.scm.service.CarrierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarrierServiceImpl extends ServiceImpl<CarrierMapper, Carrier> implements CarrierService {

    @Override
    public IPage<Carrier> getCarrierPage(Integer page, Integer limit, String name, String contact, String status) {
        LambdaQueryWrapper<Carrier> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Carrier::getName, name);
        }
        if (StringUtils.isNotBlank(contact)) {
            wrapper.like(Carrier::getContact, contact);
        }
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(Carrier::getStatus, status);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Carrier::getCreateTime);
        
        return page(new Page<>(page, limit), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableCarrier(Long id) {
        Carrier carrier = getById(id);
        if (carrier == null) {
            throw new RuntimeException("承运商不存在");
        }
        
        carrier.setStatus("normal");
        updateById(carrier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableCarrier(Long id) {
        Carrier carrier = getById(id);
        if (carrier == null) {
            throw new RuntimeException("承运商不存在");
        }
        
        carrier.setStatus("disabled");
        updateById(carrier);
    }
} 