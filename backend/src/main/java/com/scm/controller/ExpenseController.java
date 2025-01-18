package com.scm.controller;

import com.scm.entity.Expense;
import com.scm.service.ExpenseService;
import com.scm.utils.PageData;
import com.scm.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 支出控制器
 */
@RestController
@RequestMapping("/api/finance/expenses")
public class ExpenseController {
    @Resource
    private ExpenseService expenseService;

    /**
     * 获取支出列表
     *
     * @param offset    偏移量
     * @param limit     限制条数
     * @param expenseNo 支出编号
     * @param type      支出类型
     * @param status    支出状态
     * @return 支出列表和总数
     */
    @GetMapping("")
    public Result<PageData<Expense>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String expenseNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        params.put("expenseNo", expenseNo);
        params.put("type", type);
        params.put("status", status);
        PageData<Expense> pageData = expenseService.getList(params);
        return Result.success(pageData);
    }

    /**
     * 获取支出详情
     *
     * @param id 支出ID
     * @return 支出信息
     */
    @GetMapping("/{id}")
    public Result<Expense> getById(@PathVariable Long id) {
        Expense expense = expenseService.getById(id);
        return Result.success(expense);
    }

    /**
     * 创建支出
     *
     * @param expense 支出信息
     * @return 处理结果
     */
    @PostMapping("")
    public Result<?> create(@RequestBody Expense expense) {
        expenseService.create(expense);
        return Result.success();
    }

    /**
     * 更新支出
     *
     * @param id      支出ID
     * @param expense 支出信息
     * @return 处理结果
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Expense expense) {
        expense.setId(id);
        expenseService.update(expense);
        return Result.success();
    }

    /**
     * 更新支出状态
     *
     * @param id     支出ID
     * @param status 支出状态
     * @return 处理结果
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        expenseService.updateStatus(id, status);
        return Result.success();
    }
} 