package com.scm.service.impl;

import com.scm.entity.PurchaseOrder;
import com.scm.entity.PurchaseOrderItem;
import com.scm.mapper.PurchaseOrderMapper;
import com.scm.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public List<PurchaseOrder> selectList(String code, String supplier, String status, Integer offset, Integer limit) {
        return purchaseOrderMapper.selectList(code, supplier, status, offset, limit);
    }

    @Override
    public int selectCount(String code, String supplier, String status) {
        return purchaseOrderMapper.selectCount(code, supplier, status);
    }

    @Override
    public PurchaseOrder selectById(Long id) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order != null) {
            List<PurchaseOrderItem> items = purchaseOrderMapper.selectItemsByOrderId(id);
            order.setItems(items);
        }
        return order;
    }

    @Override
    @Transactional
    public void insert(PurchaseOrder order) {
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setStatus("pending");
        
        purchaseOrderMapper.insert(order);
        
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (PurchaseOrderItem item : order.getItems()) {
                item.setOrderId(order.getId());
                item.setOrderCode(order.getCode());
                item.setCreateTime(now);
                item.setUpdateTime(now);
            }
            purchaseOrderMapper.batchInsertItems(order.getItems());
        }
    }

    @Override
    @Transactional
    public void update(PurchaseOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.update(order);
        
        if (order.getItems() != null) {
            purchaseOrderMapper.deleteItemsByOrderId(order.getId());
            if (!order.getItems().isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                for (PurchaseOrderItem item : order.getItems()) {
                    item.setOrderId(order.getId());
                    item.setOrderCode(order.getCode());
                    item.setCreateTime(now);
                    item.setUpdateTime(now);
                }
                purchaseOrderMapper.batchInsertItems(order.getItems());
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        purchaseOrderMapper.deleteItemsByOrderId(id);
        purchaseOrderMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        purchaseOrderMapper.updateStatus(id, status);
    }

    @Override
    public List<PurchaseOrder> selectAvailableForInbound() {
        return purchaseOrderMapper.selectAvailableForInbound();
    }
} 