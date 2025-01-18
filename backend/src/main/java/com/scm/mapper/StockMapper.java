package com.scm.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper {
    
    /**
     * 查询库存预警商品数量
     * 当前库存小于预警库存的商品数量
     */
    int selectWarningCount();
} 