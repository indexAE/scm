package com.scm.mapper;

import com.scm.entity.LoginLog;
import com.scm.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

@Mapper
public interface LogMapper {
    // 操作日志
    void insertOperationLog(OperationLog log);
    List<OperationLog> getOperationLogs(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);
    int getOperationLogCount(@Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);
    
    // 登录日志
    void insertLoginLog(LoginLog log);
    List<LoginLog> getLoginLogs(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               @Param("offset") int offset,
                               @Param("limit") int limit);
    int getLoginLogCount(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
} 