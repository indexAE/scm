package com.scm.controller;

import com.scm.entity.Income;
import com.scm.service.IncomeService;
import com.scm.utils.PageData;
import com.scm.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 收入控制器
 */
@RestController
@RequestMapping("/api/finance/incomes")
public class IncomeController {
    @Resource
    private IncomeService incomeService;

    /**
     * 获取收入列表
     *
     * @param offset   偏移量
     * @param limit    限制条数
     * @param incomeNo 收入编号
     * @param type     收入类型
     * @param status   收入状态
     * @return 收入列表和总数
     */
    @GetMapping("")
    public Result<PageData<Income>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String incomeNo,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        params.put("incomeNo", incomeNo);
        params.put("type", type);
        params.put("status", status);
        PageData<Income> pageData = incomeService.getList(params);
        return Result.success(pageData);
    }

    /**
     * 获取收入详情
     *
     * @param id 收入ID
     * @return 收入信息
     */
    @GetMapping("/{id}")
    public Result<Income> getById(@PathVariable Long id) {
        Income income = incomeService.getById(id);
        return Result.success(income);
    }

    /**
     * 创建收入
     *
     * @param income 收入信息
     * @return 处理结果
     */
    @PostMapping("")
    public Result<?> create(@RequestBody Income income) {
        incomeService.create(income);
        return Result.success();
    }

    /**
     * 更新收入
     *
     * @param id     收入ID
     * @param income 收入信息
     * @return 处理结果
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Income income) {
        income.setId(id);
        incomeService.update(income);
        return Result.success();
    }

    /**
     * 更新收入状态
     *
     * @param id     收入ID
     * @param status 收入状态
     * @return 处理结果
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        incomeService.updateStatus(id, status);
        return Result.success();
    }
} 