package com.scm.service.impl;

import com.scm.common.PageData;
import com.scm.entity.SaleVariance;
import com.scm.entity.SaleOrder;
import com.scm.entity.Customer;
import com.scm.mapper.SaleVarianceMapper;
import com.scm.service.SaleVarianceService;
import com.scm.service.SaleOrderService;
import com.scm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleVarianceServiceImpl implements SaleVarianceService {

    @Autowired
    private SaleVarianceMapper saleVarianceMapper;
    
    @Autowired
    private SaleOrderService saleOrderService;
    
    @Autowired
    private CustomerService customerService;

    @Override
    public PageData<SaleVariance> getList(String varianceNo, String customerName, String type, String status, int offset, int limit) {
        // 查询列表
        List<SaleVariance> list = saleVarianceMapper.selectList(
            varianceNo, 
            customerName, 
            type,
            status,
            offset, 
            limit
        );
        
        // 查询总数
        int total = saleVarianceMapper.selectCount(
            varianceNo, 
            customerName, 
            type,
            status
        );
        
        // 返回分页结果
        PageData<SaleVariance> pageData = new PageData<>();
        pageData.setList(list);
        pageData.setTotal((long)total);
        return pageData;
    }

    @Override
    public SaleVariance getById(Long id) {
        return saleVarianceMapper.selectById(id);
    }

    @Override
    @Transactional
    public void create(SaleVariance variance) {
        // 获取关联订单信息
        SaleOrder order = saleOrderService.getById(variance.getOrderId());
        if (order == null) {
            throw new RuntimeException("关联订单不存在");
        }
        
        // 验证客户信息
        Customer customer = customerService.getCustomerById(order.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("客户信息不存在");
        }
        
        // 设置订单相关信息
        variance.setOrderNo(order.getOrderNo());
        variance.setCustomerId(customer.getId());
        variance.setCustomerName(customer.getName());  // 使用客户表中的名称
        variance.setStatus("pending");  // 设置初始状态为待处理
        
        saleVarianceMapper.insert(variance);
    }

    @Override
    @Transactional
    public void update(SaleVariance variance) {
        saleVarianceMapper.update(variance);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        saleVarianceMapper.updateStatus(id, status);
    }
} 