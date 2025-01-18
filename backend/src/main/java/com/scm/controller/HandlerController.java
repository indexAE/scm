package com.scm.controller;

import com.scm.common.PageData;
import com.scm.common.Result;
import com.scm.entity.Handler;
import com.scm.service.HandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/handlers")
public class HandlerController {

    @Autowired
    private HandlerService handlerService;

    @GetMapping
    public Result<PageData<Handler>> search(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        PageData<Handler> pageData = handlerService.search(offset, limit, code, name, status);
        return Result.success(pageData);
    }

    @GetMapping("/{id}")
    public Result<Handler> getById(@PathVariable Long id) {
        Handler handler = handlerService.getById(id);
        return Result.success(handler);
    }

    @PostMapping
    public Result<Handler> create(@RequestBody Handler handler) {
        handlerService.create(handler);
        return Result.success(handler);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Handler handler) {
        handler.setId(id);
        handlerService.update(handler);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        handlerService.updateStatus(id, status);
        return Result.success();
    }
} 