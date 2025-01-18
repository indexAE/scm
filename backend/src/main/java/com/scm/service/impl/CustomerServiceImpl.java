package com.scm.service.impl;

import com.scm.entity.Customer;
import com.scm.mapper.CustomerMapper;
import com.scm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Override
    public List<Customer> getAllCustomers() {
        return customerMapper.findAll();
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        return customerMapper.findById(id);
    }
    
    @Override
    public boolean createCustomer(Customer customer) {
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customer.setStatus(true); // 默认启用
        return customerMapper.insert(customer) > 0;
    }
    
    @Override
    public boolean updateCustomer(Customer customer) {
        customer.setUpdateTime(LocalDateTime.now());
        return customerMapper.update(customer) > 0;
    }
    
    @Override
    public boolean deleteCustomer(Long id) {
        return customerMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean updateCustomerStatus(Long id, Boolean status) {
        return customerMapper.updateStatus(id, status) > 0;
    }
} 