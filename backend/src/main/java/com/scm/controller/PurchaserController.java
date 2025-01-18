package com.scm.controller;

import com.scm.common.Result;
import com.scm.entity.Purchaser;
import com.scm.service.PurchaserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchasers")
public class PurchaserController {
    
    @Autowired
    private PurchaserService purchaserService;
    
    @GetMapping
    public Result list(@RequestParam(required = false) String name,
                      @RequestParam(required = false) String phone,
                      @RequestParam(required = false) String status,
                      @RequestParam(defaultValue = "0") Integer offset,
                      @RequestParam(defaultValue = "10") Integer limit) {
        List<Purchaser> list = purchaserService.selectList(name, phone, status, offset, limit);
        int total = purchaserService.selectCount(name, phone, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(purchaserService.selectById(id));
    }
    
    @PostMapping
    public Result add(@RequestBody Purchaser purchaser) {
        purchaserService.insert(purchaser);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Purchaser purchaser) {
        purchaser.setId(id);
        purchaserService.update(purchaser);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        purchaserService.updateStatus(id, status);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        purchaserService.deleteById(id);
        return Result.success();
    }
} 