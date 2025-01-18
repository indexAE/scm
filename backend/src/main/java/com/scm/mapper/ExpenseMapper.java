package com.scm.mapper;

import com.scm.entity.Expense;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 支出数据访问层接口
 */
public interface ExpenseMapper {
    /**
     * 获取支出列表
     *
     * @param params 查询参数
     * @return 支出列表
     */
    List<Expense> getList(Map<String, Object> params);

    /**
     * 获取支出总数
     *
     * @param params 查询参数
     * @return 支出总数
     */
    int getTotal(Map<String, Object> params);

    /**
     * 根据ID获取支出
     *
     * @param id 支出ID
     * @return 支出信息
     */
    Expense getById(@Param("id") Long id);

    /**
     * 新增支出
     *
     * @param expense 支出信息
     * @return 影响行数
     */
    int insert(Expense expense);

    /**
     * 更新支出
     *
     * @param expense 支出信息
     * @return 影响行数
     */
    int update(Expense expense);

    /**
     * 更新支出状态
     *
     * @param id     支出ID
     * @param status 支出状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 按日期范围查询支出记录
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 支出记录列表
     */
    List<Expense> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
} 