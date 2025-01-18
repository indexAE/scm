package com.scm.service;

import com.scm.entity.Account;
import com.scm.utils.PageData;

import java.util.Map;

/**
 * 账户服务层接口
 */
public interface AccountService {
    /**
     * 获取账户列表
     *
     * @param params 查询参数
     * @return 账户列表和总数
     */
    PageData<Account> getList(Map<String, Object> params);

    /**
     * 根据ID获取账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    Account getById(Long id);

    /**
     * 创建账户
     *
     * @param account 账户信息
     */
    void create(Account account);

    /**
     * 更新账户
     *
     * @param account 账户信息
     */
    void update(Account account);

    /**
     * 更新账户状态
     *
     * @param id     账户ID
     * @param status 账户状态
     */
    void updateStatus(Long id, String status);
} 