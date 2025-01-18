package com.scm.mapper;

import com.scm.entity.SaleReturn;
import com.scm.entity.SaleReturnItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

public interface SaleReturnMapper {
    List<SaleReturn> selectList(@Param("returnNo") String returnNo,
                               @Param("customerName") String customerName,
                               @Param("status") String status,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int selectCount(@Param("returnNo") String returnNo,
                   @Param("customerName") String customerName,
                   @Param("status") String status);

    SaleReturn selectById(@Param("id") Long id);

    String selectMaxReturnNoByPrefix(@Param("prefix") String prefix);

    int insert(SaleReturn saleReturn);

    int insertItem(SaleReturnItem item);

    int update(SaleReturn saleReturn);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int deleteItems(@Param("returnId") Long returnId);

    int delete(@Param("id") Long id);

    int selectCountByDateRange(@Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);
} 