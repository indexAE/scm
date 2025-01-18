package com.scm.service;

import com.scm.common.PageData;
import com.scm.entity.Handler;

public interface HandlerService {
    PageData<Handler> search(Integer offset, Integer limit, String code, String name, String status);
    
    Handler getById(Long id);
    
    void create(Handler handler);
    
    void update(Handler handler);
    
    void updateStatus(Long id, String status);
} 