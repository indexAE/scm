package com.scm.mapper;

import com.scm.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    // 获取所有客户
    List<Customer> findAll();
    
    // 根据ID获取客户
    Customer findById(@Param("id") Long id);
    
    // 创建客户
    int insert(Customer customer);
    
    // 更新客户
    int update(Customer customer);
    
    // 删除客户
    int deleteById(@Param("id") Long id);
    
    // 更新客户状态
    int updateStatus(@Param("id") Long id, @Param("status") Boolean status);
} 