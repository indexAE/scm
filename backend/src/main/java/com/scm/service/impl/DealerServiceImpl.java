package com.scm.service.impl;

import com.scm.entity.Dealer;
import com.scm.mapper.DealerMapper;
import com.scm.service.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DealerServiceImpl implements DealerService {

    @Autowired
    private DealerMapper dealerMapper;

    @Override
    public List<Dealer> findAll() {
        return dealerMapper.findAll();
    }

    @Override
    public Dealer findById(Long id) {
        return dealerMapper.findById(id);
    }

    @Override
    @Transactional
    public int create(Dealer dealer) {
        dealer.setStatus(true);
        return dealerMapper.insert(dealer);
    }

    @Override
    @Transactional
    public int update(Dealer dealer) {
        return dealerMapper.update(dealer);
    }

    @Override
    @Transactional
    public int deleteById(Long id) {
        return dealerMapper.deleteById(id);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Boolean status) {
        return dealerMapper.updateStatus(id, status);
    }
} 