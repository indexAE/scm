package com.scm.controller;

import com.scm.common.Result;
import com.scm.entity.Warehouse;
import com.scm.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Warehouse> list = warehouseService.selectList(name, status, offset, limit);
        int total = warehouseService.selectCount(name, status);

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);

        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Warehouse> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.selectById(id);
        return Result.success(warehouse);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Warehouse warehouse) {
        warehouseService.insert(warehouse);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseService.update(warehouse);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        warehouseService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        warehouseService.updateStatus(id, status);
        return Result.success();
    }
} 