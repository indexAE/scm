package com.scm.mapper;

import com.scm.entity.Warehouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarehouseMapper {
    List<Warehouse> selectList(@Param("name") String name, @Param("status") String status, @Param("offset") Integer offset, @Param("limit") Integer limit);

    int selectCount(@Param("name") String name, @Param("status") String status);

    Warehouse selectById(@Param("id") Long id);

    int insert(Warehouse warehouse);

    int update(Warehouse warehouse);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status);
} 