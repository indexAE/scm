package com.scm.service.impl;

import com.scm.entity.Retailer;
import com.scm.mapper.RetailerMapper;
import com.scm.service.RetailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RetailerServiceImpl implements RetailerService {

    @Autowired
    private RetailerMapper retailerMapper;

    @Override
    public List<Retailer> findAll() {
        return retailerMapper.findAll();
    }

    @Override
    public Retailer findById(Long id) {
        return retailerMapper.findById(id);
    }

    @Override
    @Transactional
    public int create(Retailer retailer) {
        retailer.setStatus(true);
        return retailerMapper.insert(retailer);
    }

    @Override
    @Transactional
    public int update(Retailer retailer) {
        return retailerMapper.update(retailer);
    }

    @Override
    @Transactional
    public int deleteById(Long id) {
        return retailerMapper.deleteById(id);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Boolean status) {
        return retailerMapper.updateStatus(id, status);
    }
} 