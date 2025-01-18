package com.scm.controller;

import com.scm.entity.Customer;
import com.scm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public Map<String, Object> getAllCustomers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Customer> customers = customerService.getAllCustomers();
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", customers);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getCustomerById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Customer customer = customerService.getCustomerById(id);
            if (customer != null) {
                result.put("code", 0);
                result.put("message", "获取成功");
                result.put("data", customer);
            } else {
                result.put("code", 1);
                result.put("message", "客户不存在");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping
    public Map<String, Object> createCustomer(@RequestBody Customer customer) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (customerService.createCustomer(customer)) {
                result.put("code", 0);
                result.put("message", "创建成功");
            } else {
                result.put("code", 1);
                result.put("message", "创建失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "创建失败: " + e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Map<String, Object> result = new HashMap<>();
        try {
            customer.setId(id);
            if (customerService.updateCustomer(customer)) {
                result.put("code", 0);
                result.put("message", "更新成功");
            } else {
                result.put("code", 1);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCustomer(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (customerService.deleteCustomer(id)) {
                result.put("code", 0);
                result.put("message", "删除成功");
            } else {
                result.put("code", 1);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/{id}/status")
    public Map<String, Object> updateCustomerStatus(@PathVariable Long id, @RequestParam Boolean status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (customerService.updateCustomerStatus(id, status)) {
                result.put("code", 0);
                result.put("message", "状态更新成功");
            } else {
                result.put("code", 1);
                result.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "状态更新失败: " + e.getMessage());
        }
        return result;
    }
} 