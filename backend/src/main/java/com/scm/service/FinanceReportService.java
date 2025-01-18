package com.scm.service;

import com.scm.vo.finance.FinanceReportVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FinanceReportService {
    /**
     * 获取财务报表数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param type      统计类型：day-按日, week-按周, month-按月
     * @return 财务报表数据
     */
    FinanceReportVO getReport(String startDate, String endDate, String type);

    /**
     * 导出财务报表
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param response  HTTP响应
     * @throws IOException IO异常
     */
    void exportReport(String startDate, String endDate, HttpServletResponse response) throws IOException;
} 