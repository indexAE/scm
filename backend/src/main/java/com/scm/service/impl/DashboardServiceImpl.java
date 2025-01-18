package com.scm.service.impl;

import com.scm.mapper.*;
import com.scm.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;
    
    @Autowired
    private SaleOrderMapper saleOrderMapper;
    
    @Autowired
    private SaleReturnMapper saleReturnMapper;
    
    @Autowired
    private StockMapper stockMapper;

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取待处理采购订单数(待审核)
        int pendingPurchaseOrders = purchaseOrderMapper.selectCount(null, null, "pending");
        
        // 获取待处理销售订单数(待审核)
        int pendingSaleOrders = saleOrderMapper.selectCount(null, null, "pending");
        
        // 获取待处理退货数(待审核)
        int pendingReturns = saleReturnMapper.selectCount(null, null, "pending");
        
        // 获取库存预警商品数
        int stockWarnings = stockMapper.selectWarningCount();
        
        stats.put("pendingPurchaseOrders", pendingPurchaseOrders);
        stats.put("pendingSaleOrders", pendingSaleOrders);
        stats.put("pendingReturns", pendingReturns);
        stats.put("stockWarnings", stockWarnings);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTrendStats() {
        Map<String, Object> stats = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> purchaseOrders = new ArrayList<>();
        List<Integer> saleOrders = new ArrayList<>();
        List<Integer> returnOrders = new ArrayList<>();
        
        // 获取最近30天的数据
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 29; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String formattedDate = date.format(formatter);
            dates.add(formattedDate);
            
            // 获取每天的订单数量
            LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
            
            purchaseOrders.add(purchaseOrderMapper.selectCountByDateRange(startOfDay, endOfDay));
            saleOrders.add(saleOrderMapper.selectCountByDateRange(startOfDay, endOfDay));
            returnOrders.add(saleReturnMapper.selectCountByDateRange(startOfDay, endOfDay));
        }
        
        stats.put("dates", dates);
        stats.put("purchaseOrders", purchaseOrders);
        stats.put("saleOrders", saleOrders);
        stats.put("returnOrders", returnOrders);
        
        return stats;
    }
} 