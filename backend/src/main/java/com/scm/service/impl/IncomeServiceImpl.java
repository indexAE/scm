package com.scm.service.impl;

import com.scm.entity.Income;
import com.scm.mapper.IncomeMapper;
import com.scm.service.IncomeService;
import com.scm.utils.PageData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 收入服务层实现类
 */
@Service
public class IncomeServiceImpl implements IncomeService {
    @Resource
    private IncomeMapper incomeMapper;

    @Override
    public PageData<Income> getList(Map<String, Object> params) {
        List<Income> list = incomeMapper.getList(params);
        int total = incomeMapper.getTotal(params);
        return new PageData<>(list, total);
    }

    @Override
    public Income getById(Long id) {
        return incomeMapper.getById(id);
    }

    @Override
    @Transactional
    public void create(Income income) {
        // 设置初始状态为待确认
        income.setStatus("pending");
        incomeMapper.insert(income);
    }

    @Override
    @Transactional
    public void update(Income income) {
        // 获取原收入信息
        Income oldIncome = incomeMapper.getById(income.getId());
        if (oldIncome == null) {
            throw new RuntimeException("收入不存在");
        }

        // 只有待确认状态的收入才能修改
        if (!"pending".equals(oldIncome.getStatus())) {
            throw new RuntimeException("收入状态不是待确认，无法修改");
        }

        incomeMapper.update(income);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        // 获取原收入信息
        Income oldIncome = incomeMapper.getById(id);
        if (oldIncome == null) {
            throw new RuntimeException("收入不存在");
        }

        // 验证状态值
        if (!isValidStatus(status)) {
            throw new RuntimeException("无效的状态值");
        }

        // 只有待确认状态的收入才能更新状态
        if (!"pending".equals(oldIncome.getStatus())) {
            throw new RuntimeException("收入状态不是待确认，无法更新状态");
        }

        // 相同状态不需要更新
        if (status.equals(oldIncome.getStatus())) {
            return;
        }

        incomeMapper.updateStatus(id, status);
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