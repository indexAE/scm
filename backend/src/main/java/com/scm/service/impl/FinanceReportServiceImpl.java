package com.scm.service.impl;

import com.scm.entity.Income;
import com.scm.entity.Expense;
import com.scm.mapper.IncomeMapper;
import com.scm.mapper.ExpenseMapper;
import com.scm.service.FinanceReportService;
import com.scm.vo.finance.FinanceReportVO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceReportServiceImpl implements FinanceReportService {

    @Autowired
    private IncomeMapper incomeMapper;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Override
    public FinanceReportVO getReport(String startDate, String endDate, String type) {
        FinanceReportVO report = new FinanceReportVO();

        // 获取当前时间段的收支数据
        List<Income> currentIncomes = incomeMapper.selectByDateRange(startDate, endDate);
        List<Expense> currentExpenses = expenseMapper.selectByDateRange(startDate, endDate);

        // 计算总收入和总支出
        BigDecimal totalIncome = currentIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = currentExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);

        // 计算同比增长率
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        String lastYearStart = start.minusYears(1).toString();
        String lastYearEnd = end.minusYears(1).toString();

        List<Income> lastYearIncomes = incomeMapper.selectByDateRange(lastYearStart, lastYearEnd);
        List<Expense> lastYearExpenses = expenseMapper.selectByDateRange(lastYearStart, lastYearEnd);

        BigDecimal lastYearIncome = lastYearIncomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal lastYearExpense = lastYearExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 计算增长率
        report.setIncomeTrend(calculateGrowthRate(lastYearIncome, totalIncome));
        report.setExpenseTrend(calculateGrowthRate(lastYearExpense, totalExpense));

        // 设置趋势数据
        FinanceReportVO.TrendData trendData = calculateTrendData(currentIncomes, currentExpenses, startDate, endDate, type);
        report.setTrend(trendData);

        // 设置收入构成
        FinanceReportVO.CompositionData incomeComposition = new FinanceReportVO.CompositionData();
        BigDecimal saleIncome = currentIncomes.stream()
                .filter(i -> "sale".equals(i.getType()))
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal otherIncome = currentIncomes.stream()
                .filter(i -> "other".equals(i.getType()))
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        incomeComposition.setSale(saleIncome);
        incomeComposition.setOther(otherIncome);
        report.setIncomeComposition(incomeComposition);

        // 设置支出构成
        FinanceReportVO.CompositionData expenseComposition = new FinanceReportVO.CompositionData();
        BigDecimal purchaseExpense = currentExpenses.stream()
                .filter(e -> "purchase".equals(e.getType()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal otherExpense = currentExpenses.stream()
                .filter(e -> "other".equals(e.getType()))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        expenseComposition.setPurchase(purchaseExpense);
        expenseComposition.setOther(otherExpense);
        report.setExpenseComposition(expenseComposition);

        return report;
    }

    @Override
    public void exportReport(String startDate, String endDate, HttpServletResponse response) throws IOException {
        // 获取报表数据
        FinanceReportVO report = getReport(startDate, endDate, "day");

        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("财务报表");

        // 创建标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.RIGHT);

        // 设置列宽
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);

        // 创建标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("财务报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 创建日期范围行
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue(String.format("统计期间：%s 至 %s", startDate, endDate));
        dateCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        // 创建汇总数据
        Row summaryHeaderRow = sheet.createRow(3);
        summaryHeaderRow.createCell(0).setCellValue("项目");
        summaryHeaderRow.createCell(1).setCellValue("金额");
        summaryHeaderRow.createCell(2).setCellValue("同比增长");
        summaryHeaderRow.getCell(0).setCellStyle(headerStyle);
        summaryHeaderRow.getCell(1).setCellStyle(headerStyle);
        summaryHeaderRow.getCell(2).setCellStyle(headerStyle);

        Row incomeRow = sheet.createRow(4);
        incomeRow.createCell(0).setCellValue("总收入");
        incomeRow.createCell(1).setCellValue(report.getTotalIncome().doubleValue());
        incomeRow.createCell(2).setCellValue(report.getIncomeTrend().doubleValue() + "%");
        incomeRow.getCell(1).setCellStyle(dataStyle);
        incomeRow.getCell(2).setCellStyle(dataStyle);

        Row expenseRow = sheet.createRow(5);
        expenseRow.createCell(0).setCellValue("总支出");
        expenseRow.createCell(1).setCellValue(report.getTotalExpense().doubleValue());
        expenseRow.createCell(2).setCellValue(report.getExpenseTrend().doubleValue() + "%");
        expenseRow.getCell(1).setCellStyle(dataStyle);
        expenseRow.getCell(2).setCellStyle(dataStyle);

        // 创建收入构成
        Row incomeCompositionHeaderRow = sheet.createRow(7);
        incomeCompositionHeaderRow.createCell(0).setCellValue("收入构成");
        incomeCompositionHeaderRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));

        Row incomeCompositionRow = sheet.createRow(8);
        incomeCompositionRow.createCell(0).setCellValue("销售收入");
        incomeCompositionRow.createCell(1).setCellValue(report.getIncomeComposition().getSale().doubleValue());
        incomeCompositionRow.getCell(1).setCellStyle(dataStyle);

        Row otherIncomeRow = sheet.createRow(9);
        otherIncomeRow.createCell(0).setCellValue("其他收入");
        otherIncomeRow.createCell(1).setCellValue(report.getIncomeComposition().getOther().doubleValue());
        otherIncomeRow.getCell(1).setCellStyle(dataStyle);

        // 创建支出构成
        Row expenseCompositionHeaderRow = sheet.createRow(11);
        expenseCompositionHeaderRow.createCell(0).setCellValue("支出构成");
        expenseCompositionHeaderRow.getCell(0).setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(11, 11, 0, 3));

        Row expenseCompositionRow = sheet.createRow(12);
        expenseCompositionRow.createCell(0).setCellValue("采购支出");
        expenseCompositionRow.createCell(1).setCellValue(report.getExpenseComposition().getPurchase().doubleValue());
        expenseCompositionRow.getCell(1).setCellStyle(dataStyle);

        Row otherExpenseRow = sheet.createRow(13);
        otherExpenseRow.createCell(0).setCellValue("其他支出");
        otherExpenseRow.createCell(1).setCellValue(report.getExpenseComposition().getOther().doubleValue());
        otherExpenseRow.getCell(1).setCellStyle(dataStyle);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", 
                String.format("attachment; filename=finance_report_%s_%s.xlsx", startDate, endDate));

        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 计算增长率
     */
    private BigDecimal calculateGrowthRate(BigDecimal base, BigDecimal current) {
        if (base.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }
        return current.subtract(base)
                .divide(base, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算趋势数据
     */
    private FinanceReportVO.TrendData calculateTrendData(
            List<Income> incomes,
            List<Expense> expenses,
            String startDate,
            String endDate,
            String type) {
        FinanceReportVO.TrendData trendData = new FinanceReportVO.TrendData();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> incomeAmounts = new ArrayList<>();
        List<BigDecimal> expenseAmounts = new ArrayList<>();
        List<BigDecimal> netIncomes = new ArrayList<>();

        // 按日期分组
        Map<String, BigDecimal> incomeMap = incomes.stream()
                .collect(Collectors.groupingBy(
                        income -> income.getCreateTime().toLocalDate().toString(),
                        Collectors.mapping(
                                Income::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        Map<String, BigDecimal> expenseMap = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCreateTime().toLocalDate().toString(),
                        Collectors.mapping(
                                Expense::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // 生成日期序列
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(formatter);
            dates.add(dateStr);
            
            BigDecimal income = incomeMap.getOrDefault(dateStr, BigDecimal.ZERO);
            BigDecimal expense = expenseMap.getOrDefault(dateStr, BigDecimal.ZERO);
            BigDecimal netIncome = income.subtract(expense);
            
            incomeAmounts.add(income);
            expenseAmounts.add(expense);
            netIncomes.add(netIncome);
        }

        trendData.setDates(dates);
        trendData.setIncomes(incomeAmounts);
        trendData.setExpenses(expenseAmounts);
        trendData.setNetIncomes(netIncomes);

        return trendData;
    }
} 