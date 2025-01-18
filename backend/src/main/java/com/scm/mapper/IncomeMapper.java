package com.scm.mapper;

import com.scm.entity.Income;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 收入数据访问层接口
 */
public interface IncomeMapper {
    /**
     * 获取收入列表
     *
     * @param params 查询参数
     * @return 收入列表
     */
    List<Income> getList(Map<String, Object> params);

    /**
     * 获取收入总数
     *
     * @param params 查询参数
     * @return 收入总数
     */
    int getTotal(Map<String, Object> params);

    /**
     * 根据ID获取收入
     *
     * @param id 收入ID
     * @return 收入信息
     */
    Income getById(@Param("id") Long id);

    /**
     * 新增收入
     *
     * @param income 收入信息
     * @return 影响行数
     */
    int insert(Income income);

    /**
     * 更新收入
     *
     * @param income 收入信息
     * @return 影响行数
     */
    int update(Income income);

    /**
     * 更新收入状态
     *
     * @param id     收入ID
     * @param status 收入状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 按日期范围查询收入记录
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 收入记录列表
     */
    List<Income> selectByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
} 