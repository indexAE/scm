package com.scm.service.impl;

import com.scm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.scm.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public BigDecimal getDailySales(LocalDate date) {
        // 获取指定日期的销售总额
        BigDecimal dailySales = orderMapper.getDailySales(date);
        return dailySales != null ? dailySales : BigDecimal.ZERO;
    }
    
    @Override
    public List<Map<String, Object>> getProductSales() {
        // 获取最近30天的商品销量数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        return orderMapper.getProductSales(startDate, endDate);
    }
    
    @Override
    public Map<String, Object> getAmountCompare() {
        // 获取当前月份和上个月份
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);
        
        logger.info("Fetching amount data for current month: {} and last month: {}", 
            currentMonth, lastMonth);
        
        // 获取本月数据
        Map<String, BigDecimal> currentMonthData = orderMapper.getMonthlyAmount(
            currentMonth.atDay(1),
            currentMonth.atEndOfMonth()
        );
        
        // 获取上月数据
        Map<String, BigDecimal> lastMonthData = orderMapper.getMonthlyAmount(
            lastMonth.atDay(1),
            lastMonth.atEndOfMonth()
        );
        
        logger.info("Current month data: {}", currentMonthData);
        logger.info("Last month data: {}", lastMonthData);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> currentMonthMap = new HashMap<>();
        Map<String, Object> lastMonthMap = new HashMap<>();
        
        currentMonthMap.put("purchase", currentMonthData.get("purchase_amount"));
        currentMonthMap.put("sale", currentMonthData.get("sale_amount"));
        currentMonthMap.put("return", currentMonthData.get("return_amount"));
        
        lastMonthMap.put("purchase", lastMonthData.get("purchase_amount"));
        lastMonthMap.put("sale", lastMonthData.get("sale_amount"));
        lastMonthMap.put("return", lastMonthData.get("return_amount"));
        
        result.put("currentMonth", currentMonthMap);
        result.put("lastMonth", lastMonthMap);
        
        logger.info("Final result: {}", result);
        
        return result;
    }
} 