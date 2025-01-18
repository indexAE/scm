package com.scm.service;

import com.scm.entity.Income;
import com.scm.utils.PageData;

import java.util.Map;

/**
 * 收入服务层接口
 */
public interface IncomeService {
    /**
     * 获取收入列表
     *
     * @param params 查询参数
     * @return 收入列表和总数
     */
    PageData<Income> getList(Map<String, Object> params);

    /**
     * 根据ID获取收入
     *
     * @param id 收入ID
     * @return 收入信息
     */
    Income getById(Long id);

    /**
     * 创建收入
     *
     * @param income 收入信息
     */
    void create(Income income);

    /**
     * 更新收入
     *
     * @param income 收入信息
     */
    void update(Income income);

    /**
     * 更新收入状态
     *
     * @param id     收入ID
     * @param status 收入状态
     */
    void updateStatus(Long id, String status);
} 