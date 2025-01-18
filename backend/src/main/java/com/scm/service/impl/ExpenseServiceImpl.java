package com.scm.service.impl;

import com.scm.entity.Expense;
import com.scm.mapper.ExpenseMapper;
import com.scm.service.ExpenseService;
import com.scm.utils.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 支出服务层实现类
 */
@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Resource
    private ExpenseMapper expenseMapper;

    @Override
    public PageData<Expense> getList(Map<String, Object> params) {
        List<Expense> list = expenseMapper.getList(params);
        int total = expenseMapper.getTotal(params);
        return new PageData<>(list, total);
    }

    @Override
    public Expense getById(Long id) {
        return expenseMapper.getById(id);
    }

    @Override
    @Transactional
    public void create(Expense expense) {
        // 设置初始状态为待确认
        expense.setStatus("pending");
        expenseMapper.insert(expense);
    }

    @Override
    @Transactional
    public void update(Expense expense) {
        // 获取原支出信息
        Expense oldExpense = expenseMapper.getById(expense.getId());
        if (oldExpense == null) {
            throw new RuntimeException("支出不存在");
        }

        // 只有待确认状态的支出才能修改
        if (!"pending".equals(oldExpense.getStatus())) {
            throw new RuntimeException("支出状态不是待确认，无法修改");
        }

        expenseMapper.update(expense);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        // 获取原支出信息
        Expense oldExpense = expenseMapper.getById(id);
        if (oldExpense == null) {
            throw new RuntimeException("支出不存在");
        }

        // 验证状态值
        if (!isValidStatus(status)) {
            throw new RuntimeException("无效的状态值");
        }

        // 只有待确认状态的支出才能更新状态
        if (!"pending".equals(oldExpense.getStatus())) {
            throw new RuntimeException("支出状态不是待确认，无法更新状态");
        }

        // 相同状态不需要更新
        if (status.equals(oldExpense.getStatus())) {
            return;
        }

        expenseMapper.updateStatus(id, status);
    }

    /**
     * 验证状态值是否有效
     *
     * @param status 状态值
     * @return 是否有效
     */
    private boolean isValidStatus(String status) {
        return "pending".equals(status) || "confirmed".equals(status) || "cancelled".equals(status);
    }
} 