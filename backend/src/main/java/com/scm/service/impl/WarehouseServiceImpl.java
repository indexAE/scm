package com.scm.service.impl;

import com.scm.entity.Warehouse;
import com.scm.mapper.WarehouseMapper;
import com.scm.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public List<Warehouse> selectList(String name, String status, Integer offset, Integer limit) {
        return warehouseMapper.selectList(name, status, offset, limit);
    }

    @Override
    public int selectCount(String name, String status) {
        return warehouseMapper.selectCount(name, status);
    }

    @Override
    public Warehouse selectById(Long id) {
        return warehouseMapper.selectById(id);
    }

    @Override
    @Transactional
    public void insert(Warehouse warehouse) {
        warehouseMapper.insert(warehouse);
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
        warehouseMapper.update(warehouse);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        warehouseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        warehouseMapper.updateStatus(id, status);
    }
} 