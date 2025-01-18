package com.scm.mapper;

import com.scm.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 账户数据访问层接口
 */
public interface AccountMapper {
    /**
     * 获取账户列表
     *
     * @param params 查询参数
     * @return 账户列表
     */
    List<Account> getList(Map<String, Object> params);

    /**
     * 获取账户总数
     *
     * @param params 查询参数
     * @return 账户总数
     */
    int getTotal(Map<String, Object> params);

    /**
     * 根据ID获取账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    Account getById(@Param("id") Long id);

    /**
     * 新增账户
     *
     * @param account 账户信息
     * @return 影响行数
     */
    int insert(Account account);

    /**
     * 更新账户
     *
     * @param account 账户信息
     * @return 影响行数
     */
    int update(Account account);

    /**
     * 更新账户状态
     *
     * @param id     账户ID
     * @param status 账户状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
} 