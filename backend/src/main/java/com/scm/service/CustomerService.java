package com.scm.service;

import com.scm.entity.Customer;
import java.util.List;

public interface CustomerService {
    // 获取所有客户
    List<Customer> getAllCustomers();
    
    // 根据ID获取客户
    Customer getCustomerById(Long id);
    
    // 创建客户
    boolean createCustomer(Customer customer);
    
    // 更新客户
    boolean updateCustomer(Customer customer);
    
    // 删除客户
    boolean deleteCustomer(Long id);
    
    // 更新客户状态
    boolean updateCustomerStatus(Long id, Boolean status);
} 