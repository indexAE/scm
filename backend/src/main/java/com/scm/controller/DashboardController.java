package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.DashboardService;
import com.scm.service.OrderService;
import com.scm.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/scm-stats")
    public Result<Map<String, Object>> getScmStats() {
        return Result.success(dashboardService.getStats());
    }

    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrendStats() {
        return Result.success(dashboardService.getTrendStats());
    }

    @GetMapping("/growth-trend")
    public Result<Map<String, Object>> getGrowthTrend() {
        // 获取最近30天的日期列表
        List<String> dates = new ArrayList<>();
        List<BigDecimal> growthRates = new ArrayList<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate.format(formatter));
            
            // 计算当天相比前一天的销售额增长率
            LocalDate previousDate = currentDate.minusDays(1);
            BigDecimal todaySales = orderService.getDailySales(currentDate);
            BigDecimal yesterdaySales = orderService.getDailySales(previousDate);
            
            BigDecimal growthRate;
            if (yesterdaySales.compareTo(BigDecimal.ZERO) == 0) {
                growthRate = todaySales.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
            } else {
                growthRate = todaySales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
            
            growthRates.add(growthRate);
            currentDate = currentDate.plusDays(1);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("growthRates", growthRates);
        
        return Result.success(result);
    }

    @GetMapping("/product-sales")
    public Result<List<Map<String, Object>>> getProductSales() {
        // 获取商品销量占比数据
        List<Map<String, Object>> productSales = orderService.getProductSales();
        return Result.success(productSales);
    }

    @GetMapping("/amount-compare")
    public Result<Map<String, Object>> getAmountCompare() {
        // 获取本月和上月的业务金额对比数据
        Map<String, Object> amountData = orderService.getAmountCompare();
        return Result.success(amountData);
    }
    
    /**
     * 获取库存预警商品TOP5
     */
    @GetMapping("/stock-warning-top5")
    public Result<List<Map<String, Object>>> getStockWarningTop5() {
        List<Map<String, Object>> data = orderMapper.getStockWarningProducts();
        return Result.success(data);
    }
} 