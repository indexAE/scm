package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductCategory {
    private Long id;
    private Long parentId;      // 父分类ID
    private String name;        // 分类名称
    private String code;        // 分类编码
    private Integer sort;       // 排序
    private String status;      // 状态：enabled-启用，disabled-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
    
    private List<ProductCategory> children; // 子分类列表
} 