package com.scm.mapper;

import com.scm.entity.Notice;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface NoticeMapper {
    // 插入通知
    int insert(Notice notice);

    // 更新通知
    int update(Notice notice);

    // 删除通知
    int deleteById(Long id);

    // 根据ID查询通知
    Notice selectById(Long id);

    // 查询通知列表
    List<Notice> selectList(
        @Param("title") String title,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime,
        @Param("offset") Integer offset,
        @Param("limit") Integer limit
    );

    // 查询总数
    int selectCount(
        @Param("title") String title,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );
} 