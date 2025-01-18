package com.scm.service;

import com.scm.entity.Expense;
import com.scm.utils.PageData;

import java.util.Map;

/**
 * 支出服务层接口
 */
public interface ExpenseService {
    /**
     * 获取支出列表
     *
     * @param params 查询参数
     * @return 支出列表和总数
     */
    PageData<Expense> getList(Map<String, Object> params);

    /**
     * 根据ID获取支出
     *
     * @param id 支出ID
     * @return 支出信息
     */
    Expense getById(Long id);

    /**
     * 创建支出
     *
     * @param expense 支出信息
     */
    void create(Expense expense);

    /**
     * 更新支出
     *
     * @param expense 支出信息
     */
    void update(Expense expense);

    /**
     * 更新支出状态
     *
     * @param id     支出ID
     * @param status 支出状态
     */
    void updateStatus(Long id, String status);
} 