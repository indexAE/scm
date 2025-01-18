package com.scm.controller;

import com.scm.entity.Dealer;
import com.scm.service.DealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dealers")
public class DealerController {

    @Autowired
    private DealerService dealerService;

    @GetMapping
    public Map<String, Object> findAll() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Dealer> dealers = dealerService.findAll();
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", dealers);
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
            Dealer dealer = dealerService.findById(id);
            if (dealer != null) {
                result.put("code", 0);
                result.put("message", "获取成功");
                result.put("data", dealer);
            } else {
                result.put("code", 1);
                result.put("message", "经销商不存在");
            }
        } catch (Exception e) {
            result.put("code", 1);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Dealer dealer) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = dealerService.create(dealer);
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
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Dealer dealer) {
        Map<String, Object> result = new HashMap<>();
        try {
            dealer.setId(id);
            int rows = dealerService.update(dealer);
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
            int rows = dealerService.deleteById(id);
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
            int rows = dealerService.updateStatus(id, status);
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