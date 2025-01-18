package com.scm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 获取指定日期的销售总额
     * @param date 日期
     * @return 销售总额
     */
    BigDecimal getDailySales(LocalDate date);
    
    /**
     * 获取商品销量占比数据
     * @return 商品销量数据列表
     */
    List<Map<String, Object>> getProductSales();
    
    /**
     * 获取本月和上月的业务金额对比数据
     * @return 业务金额对比数据
     */
    Map<String, Object> getAmountCompare();
} 