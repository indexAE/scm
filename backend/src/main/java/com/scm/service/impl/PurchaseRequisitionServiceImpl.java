package com.scm.service.impl;

import com.scm.entity.PurchaseRequisition;
import com.scm.entity.PurchaseRequisitionItem;
import com.scm.mapper.PurchaseRequisitionMapper;
import com.scm.service.PurchaseRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PurchaseRequisitionServiceImpl implements PurchaseRequisitionService {

    @Autowired
    private PurchaseRequisitionMapper purchaseRequisitionMapper;

    @Override
    @Transactional
    public void insert(PurchaseRequisition requisition) {
        Date now = new Date();
        requisition.setCreateTime(now);
        requisition.setUpdateTime(now);
        requisition.setStatus("pending");
        
        // 插入主表数据
        purchaseRequisitionMapper.insert(requisition);
        
        // 插入明细表数据
        if (requisition.getItems() != null && !requisition.getItems().isEmpty()) {
            for (PurchaseRequisitionItem item : requisition.getItems()) {
                item.setRequisitionId(requisition.getId());
                item.setRequisitionCode(requisition.getCode());
                item.setCreateTime(now);
                item.setUpdateTime(now);
            }
            purchaseRequisitionMapper.batchInsertItems(requisition.getItems());
        }
    }

    @Override
    @Transactional
    public void update(PurchaseRequisition requisition) {
        requisition.setUpdateTime(new Date());
        purchaseRequisitionMapper.update(requisition);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        purchaseRequisitionMapper.deleteById(id);
    }

    @Override
    public PurchaseRequisition selectById(Long id) {
        return purchaseRequisitionMapper.selectById(id);
    }

    @Override
    public PurchaseRequisition selectByCode(String code) {
        return purchaseRequisitionMapper.selectByCode(code);
    }

    @Override
    public List<PurchaseRequisition> selectList(String code, String status, Integer offset, Integer limit) {
        return purchaseRequisitionMapper.selectList(code, status, offset, limit);
    }

    @Override
    public int selectCount(String code, String status) {
        return purchaseRequisitionMapper.selectCount(code, status);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status, String remark) {
        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setId(id);
        requisition.setStatus(status);
        requisition.setRemark(remark);
        requisition.setUpdateTime(new Date());
        purchaseRequisitionMapper.updateStatus(requisition);
    }
} 