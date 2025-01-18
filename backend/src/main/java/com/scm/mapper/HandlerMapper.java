package com.scm.mapper;

import com.scm.entity.Handler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface HandlerMapper {
    int insert(Handler handler);
    
    int update(Handler handler);
    
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    Handler selectById(@Param("id") Long id);
    
    List<Handler> selectList(@Param("code") String code,
                           @Param("name") String name,
                           @Param("status") String status,
                           @Param("offset") Integer offset,
                           @Param("limit") Integer limit);
    
    int selectCount(@Param("code") String code,
                   @Param("name") String name,
                   @Param("status") String status);
} 