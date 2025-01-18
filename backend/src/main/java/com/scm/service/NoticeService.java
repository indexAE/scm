package com.scm.service;

import com.scm.entity.Notice;
import java.util.List;
import java.util.Map;

public interface NoticeService {
    // 创建通知
    void createNotice(Notice notice);

    // 更新通知
    void updateNotice(Notice notice);

    // 删除通知
    void deleteNotice(Long id);

    // 获取通知详情
    Notice getNoticeById(Long id);

    // 获取通知列表
    Map<String, Object> getNoticeList(String title, String startTime, String endTime, Integer page, Integer size);
} 