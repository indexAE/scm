package com.scm.vo.finance;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FinanceReportVO {
    // 总收入
    private BigDecimal totalIncome;
    // 总支出
    private BigDecimal totalExpense;
    // 收入同比增长率
    private BigDecimal incomeTrend;
    // 支出同比增长率
    private BigDecimal expenseTrend;

    // 趋势数据
    private TrendData trend;
    // 收入构成
    private CompositionData incomeComposition;
    // 支出构成
    private CompositionData expenseComposition;

    @Data
    public static class TrendData {
        private List<String> dates;
        private List<BigDecimal> incomes;
        private List<BigDecimal> expenses;
        private List<BigDecimal> netIncomes;
    }

    @Data
    public static class CompositionData {
        private BigDecimal sale;
        private BigDecimal purchase;
        private BigDecimal other;
    }
} 