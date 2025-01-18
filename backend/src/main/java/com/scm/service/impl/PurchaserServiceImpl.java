package com.scm.service.impl;

import com.scm.entity.Purchaser;
import com.scm.mapper.PurchaserMapper;
import com.scm.service.PurchaserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaserServiceImpl implements PurchaserService {
    
    @Autowired
    private PurchaserMapper purchaserMapper;
    
    @Override
    public List<Purchaser> selectList(String name, String phone, String status, Integer offset, Integer limit) {
        return purchaserMapper.selectList(name, phone, status, offset, limit);
    }
    
    @Override
    public int selectCount(String name, String phone, String status) {
        return purchaserMapper.selectCount(name, phone, status);
    }
    
    @Override
    public Purchaser selectById(Long id) {
        return purchaserMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public void insert(Purchaser purchaser) {
        purchaser.setStatus("enabled");  // 默认启用
        purchaserMapper.insert(purchaser);
    }
    
    @Override
    @Transactional
    public void update(Purchaser purchaser) {
        purchaserMapper.update(purchaser);
    }
    
    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        purchaserMapper.updateStatus(id, status);
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        purchaserMapper.deleteById(id);
    }
} 