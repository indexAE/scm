package com.scm.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支出实体类
 */
public class Expense {
    private Long id;
    private String expenseNo;  // 支出编号
    private String type;  // 支出类型：purchase-采购支出, other-其他支出
    private BigDecimal amount;  // 支出金额
    private String status;  // 支出状态：pending-待确认, confirmed-已确认, cancelled-已取消
    private String remark;  // 备注
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpenseNo() {
        return expenseNo;
    }

    public void setExpenseNo(String expenseNo) {
        this.expenseNo = expenseNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
} 