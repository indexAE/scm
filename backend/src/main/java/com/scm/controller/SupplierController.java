package com.scm.controller;

import com.scm.entity.Supplier;
import com.scm.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    @GetMapping
    public Map<String, Object> getAllSuppliers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", suppliers);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getSupplierById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Supplier supplier = supplierService.getSupplierById(id);
            if (supplier != null) {
                result.put("code", 0);
                result.put("message", "获取成功");
                result.put("data", supplier);
            } else {
                result.put("code", 1);
                result.put("message", "供应商不存在");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping
    public Map<String, Object> createSupplier(@RequestBody Supplier supplier) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (supplierService.createSupplier(supplier)) {
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
    public Map<String, Object> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        Map<String, Object> result = new HashMap<>();
        try {
            supplier.setId(id);
            if (supplierService.updateSupplier(supplier)) {
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
    public Map<String, Object> deleteSupplier(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (supplierService.deleteSupplier(id)) {
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
    public Map<String, Object> updateSupplierStatus(@PathVariable Long id, @RequestParam Boolean status) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (supplierService.updateSupplierStatus(id, status)) {
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