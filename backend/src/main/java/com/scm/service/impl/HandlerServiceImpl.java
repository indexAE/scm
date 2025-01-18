package com.scm.service.impl;

import com.scm.entity.Handler;
import com.scm.mapper.HandlerMapper;
import com.scm.service.HandlerService;
import com.scm.common.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HandlerServiceImpl implements HandlerService {

    @Autowired
    private HandlerMapper handlerMapper;

    @Override
    public PageData<Handler> search(Integer offset, Integer limit, String code, String name, String status) {
        List<Handler> list = handlerMapper.selectList(code, name, status, offset, limit);
        long total = handlerMapper.selectCount(code, name, status);
        return new PageData<>(list, total, offset / limit + 1, limit);
    }

    @Override
    public Handler getById(Long id) {
        return handlerMapper.selectById(id);
    }

    @Override
    public void create(Handler handler) {
        String code = "H" + String.format("%08d", System.currentTimeMillis() % 100000000);
        handler.setCode(code);
        handler.setStatus("enabled");
        handlerMapper.insert(handler);
    }

    @Override
    public void update(Handler handler) {
        handlerMapper.update(handler);
    }

    @Override
    public void updateStatus(Long id, String status) {
        handlerMapper.updateStatus(id, status);
    }
} 