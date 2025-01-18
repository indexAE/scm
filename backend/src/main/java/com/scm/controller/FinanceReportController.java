package com.scm.controller;

import com.scm.common.Result;
import com.scm.service.FinanceReportService;
import com.scm.vo.finance.FinanceReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/finance/report")
public class FinanceReportController {

    @Autowired
    private FinanceReportService financeReportService;

    @GetMapping
    public Result<FinanceReportVO> getReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "day") String type) {
        return Result.success(financeReportService.getReport(startDate, endDate, type));
    }

    @GetMapping("/export")
    public void exportReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletResponse response) throws IOException {
        financeReportService.exportReport(startDate, endDate, response);
    }
} 