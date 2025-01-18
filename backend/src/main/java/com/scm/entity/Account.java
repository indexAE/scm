package com.scm.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户实体类
 */
public class Account {
    private Long id;
    private String name;  // 账户名称
    private String type;  // 账户类型：cash-现金, bank-银行卡, other-其他
    private BigDecimal balance;  // 账户余额
    private String status;  // 账户状态：normal-正常, frozen-冻结
    private String remark;  // 备注
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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