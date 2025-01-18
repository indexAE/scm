package com.scm.service.impl;

import com.scm.entity.SaleOrder;
import com.scm.entity.SaleOrderItem;
import com.scm.mapper.SaleOrderMapper;
import com.scm.service.SaleOrderService;
import com.scm.common.PageData;
import com.scm.service.CustomerService;
import com.scm.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class SaleOrderServiceImpl implements SaleOrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(SaleOrderServiceImpl.class);
    
    @Autowired
    private SaleOrderMapper saleOrderMapper;
    
    @Autowired
    private CustomerService customerService;
    
    @Override
    public PageData<SaleOrder> getList(String orderNo, String customerName, String status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SaleOrder> list = saleOrderMapper.selectList(orderNo, customerName, status, offset, pageSize);
        int total = saleOrderMapper.selectCount(orderNo, customerName, status);
        return new PageData<>(list, total, pageNum, pageSize);
    }
    
    @Override
    public SaleOrder getById(Long id) {
        return saleOrderMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public boolean create(SaleOrder order) {
        try {
            // 记录输入参数
            logger.info("Creating sale order with data: {}", order);
            if(order.getItems() != null) {
                logger.info("Order items: {}", order.getItems());
            }
            
            // 获取最新的客户信息
            Customer customer = customerService.getCustomerById(order.getCustomerId());
            if (customer == null) {
                logger.error("Customer not found with id: {}", order.getCustomerId());
                return false;
            }
            order.setCustomerName(customer.getName());
            
            // 生成订单号 SO + 年月日 + 4位序号
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String prefix = "SO" + dateStr;
            String maxOrderNo = saleOrderMapper.selectMaxOrderNoByPrefix(prefix);
            
            int sequence = 1;
            if (maxOrderNo != null) {
                // 提取序号部分并加1
                sequence = Integer.parseInt(maxOrderNo.substring(maxOrderNo.length() - 4)) + 1;
            }
            
            String orderNo = prefix + String.format("%04d", sequence);
            order.setOrderNo(orderNo);
            
            // 插入订单主表
            logger.info("Inserting main order with orderNo: {}", orderNo);
            if(saleOrderMapper.insert(order) <= 0) {
                logger.error("Failed to insert main order");
                return false;
            }
            
            // 插入订单项
            if(order.getItems() != null && !order.getItems().isEmpty()) {
                for(SaleOrderItem item : order.getItems()) {
                    logger.info("Inserting order item: {}", item);
                    item.setOrderId(order.getId());
                    if(saleOrderMapper.insertItem(item) <= 0) {
                        logger.error("Failed to insert order item: {}", item);
                        return false;
                    }
                }
            }
            
            logger.info("Successfully created sale order with id: {}", order.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error creating sale order", e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public boolean update(SaleOrder order) {
        return saleOrderMapper.update(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        return saleOrderMapper.updateStatus(id, status) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 先删除订单项
        saleOrderMapper.deleteItems(id);
        // 再删除订单
        return saleOrderMapper.delete(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean approve(Long id, String approveStatus, String remark) {
        try {
            SaleOrder order = saleOrderMapper.selectById(id);
            if (order == null) {
                logger.error("Order not found with id: {}", id);
                return false;
            }
            
            // 只有待审核状态的订单可以审核
            if (!"pending".equals(order.getStatus())) {
                logger.error("Order status is not pending, current status: {}", order.getStatus());
                return false;
            }
            
            // 更新订单状态
            order.setStatus(approveStatus);
            order.setRemark(remark);
            order.setUpdateTime(new Date());
            
            logger.info("Approving order: {}, status: {}", id, approveStatus);
            return saleOrderMapper.update(order) > 0;
        } catch (Exception e) {
            logger.error("Error approving order", e);
            throw e;
        }
    }
} 