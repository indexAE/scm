package com.scm.service.impl;

import com.scm.entity.Account;
import com.scm.mapper.AccountMapper;
import com.scm.service.AccountService;
import com.scm.utils.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 账户服务层实现类
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;

    @Override
    public PageData<Account> getList(Map<String, Object> params) {
        List<Account> list = accountMapper.getList(params);
        int total = accountMapper.getTotal(params);
        return new PageData<>(list, total);
    }

    @Override
    public Account getById(Long id) {
        return accountMapper.getById(id);
    }

    @Override
    @Transactional
    public void create(Account account) {
        // 设置初始状态为正常
        account.setStatus("normal");
        accountMapper.insert(account);
    }

    @Override
    @Transactional
    public void update(Account account) {
        // 获取原账户信息
        Account oldAccount = accountMapper.getById(account.getId());
        if (oldAccount == null) {
            throw new RuntimeException("账户不存在");
        }

        // 只有正常状态的账户才能修改
        if (!"normal".equals(oldAccount.getStatus())) {
            throw new RuntimeException("账户状态不正常，无法修改");
        }

        accountMapper.update(account);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        // 获取原账户信息
        Account oldAccount = accountMapper.getById(id);
        if (oldAccount == null) {
            throw new RuntimeException("账户不存在");
        }

        // 验证状态值
        if (!isValidStatus(status)) {
            throw new RuntimeException("无效的状态值");
        }

        // 相同状态不需要更新
        if (status.equals(oldAccount.getStatus())) {
            return;
        }

        accountMapper.updateStatus(id, status);
    }

    /**
     * 验证状态值是否有效
     *
     * @param status 状态值
     * @return 是否有效
     */
    private boolean isValidStatus(String status) {
        return "normal".equals(status) || "frozen".equals(status);
    }
} 