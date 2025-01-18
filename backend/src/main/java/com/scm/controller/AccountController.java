package com.scm.controller;

import com.scm.entity.Account;
import com.scm.service.AccountService;
import com.scm.utils.PageData;
import com.scm.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 账户控制器
 */
@RestController
@RequestMapping("/api/finance/accounts")
public class AccountController {
    @Resource
    private AccountService accountService;

    /**
     * 获取账户列表
     *
     * @param offset 偏移量
     * @param limit  限制条数
     * @param name   账户名称
     * @param type   账户类型
     * @param status 账户状态
     * @return 账户列表和总数
     */
    @GetMapping("")
    public Result<PageData<Account>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", limit);
        params.put("name", name);
        params.put("type", type);
        params.put("status", status);
        PageData<Account> pageData = accountService.getList(params);
        return Result.success(pageData);
    }

    /**
     * 获取账户详情
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @GetMapping("/{id}")
    public Result<Account> getById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        return Result.success(account);
    }

    /**
     * 创建账户
     *
     * @param account 账户信息
     * @return 处理结果
     */
    @PostMapping("")
    public Result<?> create(@RequestBody Account account) {
        accountService.create(account);
        return Result.success();
    }

    /**
     * 更新账户
     *
     * @param id      账户ID
     * @param account 账户信息
     * @return 处理结果
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Account account) {
        account.setId(id);
        accountService.update(account);
        return Result.success();
    }

    /**
     * 更新账户状态
     *
     * @param id     账户ID
     * @param status 账户状态
     * @return 处理结果
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        accountService.updateStatus(id, status);
        return Result.success();
    }
} 