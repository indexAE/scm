package com.scm.controller;

import com.scm.entity.Retailer;
import com.scm.service.RetailerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retailers")
public class RetailerController {

    @Autowired
    private RetailerService retailerService;

    @GetMapping
    public Map<String, Object> findAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Retailer> retailers = retailerService.findAll();
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", retailers);
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> findById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Retailer retailer = retailerService.findById(id);
            if (retailer != null) {
                result.put("code", 0);
                result.put("message", "获取成功");
                result.put("data", retailer);
            } else {
                result.put("code", 1);
                result.put("message", "零售商不存在");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Retailer retailer) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = retailerService.create(retailer);
            if (rows > 0) {
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
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Retailer retailer) {
        Map<String, Object> result = new HashMap<>();
        try {
            retailer.setId(id);
            int rows = retailerService.update(retailer);
            if (rows > 0) {
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
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = retailerService.deleteById(id);
            if (rows > 0) {
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
    public Map<String, Object> updateStatus(@PathVariable Long id, @RequestParam Boolean status) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = retailerService.updateStatus(id, status);
            if (rows > 0) {
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