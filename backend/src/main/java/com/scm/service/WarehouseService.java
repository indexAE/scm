package com.scm.service;

import com.scm.entity.Warehouse;
import java.util.List;

public interface WarehouseService {
    List<Warehouse> selectList(String name, String status, Integer offset, Integer limit);
    
    int selectCount(String name, String status);
    
    Warehouse selectById(Long id);
    
    void insert(Warehouse warehouse);
    
    void update(Warehouse warehouse);
    
    void deleteById(Long id);
    
    void updateStatus(Long id, String status);
} 