package com.scm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 获取指定日期的销售总额
     * @param date 日期
     * @return 销售总额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM sale_order " +
            "WHERE DATE(create_time) = #{date} AND status != 'cancelled'")
    BigDecimal getDailySales(LocalDate date);
    
    /**
     * 获取指定时间范围内的商品销量占比数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 商品销量数据列表
     */
    @Select("SELECT p.name, COUNT(soi.product_id) as sales " +
            "FROM sale_order_item soi " +
            "JOIN sale_order so ON soi.order_id = so.id " +
            "JOIN product p ON soi.product_id = p.id " +
            "WHERE DATE(so.create_time) BETWEEN #{startDate} AND #{endDate} " +
            "AND so.status != 'cancelled' " +
            "GROUP BY soi.product_id, p.name " +
            "ORDER BY sales DESC")
    List<Map<String, Object>> getProductSales(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取指定月份的各类业务金额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 业务金额数据
     */
    @Select({
        "<script>",
        "SELECT",
        "  COALESCE(SUM(CASE WHEN type = 'purchase' THEN amount ELSE 0 END), 0) as purchase_amount,",
        "  COALESCE(SUM(CASE WHEN type = 'sale' THEN amount ELSE 0 END), 0) as sale_amount,",
        "  COALESCE(SUM(CASE WHEN type = 'return' THEN amount ELSE 0 END), 0) as return_amount",
        "FROM (",
        "  SELECT amount, 'purchase' as type, create_time FROM purchase_order WHERE status != 'cancelled'",
        "  UNION ALL",
        "  SELECT total_amount as amount, 'sale' as type, create_time FROM sale_order WHERE status != 'cancelled'",
        "  UNION ALL",
        "  SELECT total_amount as amount, 'return' as type, create_time FROM sale_return WHERE status != 'cancelled'",
        ") t",
        "WHERE DATE(create_time) BETWEEN #{startDate} AND #{endDate}",
        "</script>"
    })
    Map<String, BigDecimal> getMonthlyAmount(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取库存预警商品TOP5
     * @return 库存预警商品列表
     */
    @Select("SELECT p.name as name, ws.quantity as stock, " +
            "CASE WHEN ws.min_stock = 0 THEN 10 ELSE ws.min_stock END as threshold, " +
            "CASE WHEN ws.min_stock = 0 THEN (10 - ws.quantity) " +
            "     ELSE (ws.min_stock - ws.quantity) END as warning " +
            "FROM product p " +
            "JOIN warehouse_stock ws ON p.id = ws.product_id " +
            "WHERE (ws.min_stock > 0 AND ws.quantity < ws.min_stock) " +
            "   OR (ws.min_stock = 0 AND ws.quantity < 10) " +
            "AND ws.status = 'normal' " +
            "ORDER BY warning DESC " +
            "LIMIT 5")
    List<Map<String, Object>> getStockWarningProducts();
} 