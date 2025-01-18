package com.scm.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private String scope;          // 接收范围：all-全部用户，role-指定角色，department-指定部门
    private String receivers;      // 接收对象，JSON数组字符串
    private String attachments;    // 附件信息，JSON数组字符串
    private String status;         // 状态：draft-草稿，published-已发布
    private Long publisherId;      // 发布人ID
    private String publisherName;  // 发布人姓名
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 