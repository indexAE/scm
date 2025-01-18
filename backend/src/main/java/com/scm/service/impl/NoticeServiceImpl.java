package com.scm.service.impl;

import com.scm.entity.Notice;
import com.scm.mapper.NoticeMapper;
import com.scm.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    @Transactional
    public void createNotice(Notice notice) {
        noticeMapper.insert(notice);
    }

    @Override
    @Transactional
    public void updateNotice(Notice notice) {
        noticeMapper.update(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public Notice getNoticeById(Long id) {
        return noticeMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getNoticeList(String title, String startTime, String endTime, Integer page, Integer size) {
        // 计算分页参数
        Integer offset = (page - 1) * size;
        
        // 查询数据
        List<Notice> notices = noticeMapper.selectList(title, startTime, endTime, offset, size);
        Integer total = noticeMapper.selectCount(title, startTime, endTime);
        
        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", notices);
        result.put("total", total);
        
        return result;
    }
} 