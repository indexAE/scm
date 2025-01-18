package com.scm.controller;

import com.scm.common.api.CommonResult;
import com.scm.entity.ProductCategory;
import com.scm.service.ProductCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/categories")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public CommonResult<ProductCategory> getById(@PathVariable Long id) {
        return CommonResult.success(categoryService.getById(id));
    }

    @GetMapping
    public CommonResult<List<ProductCategory>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        return CommonResult.success(categoryService.search(name, status));
    }

    @GetMapping("/tree")
    public CommonResult<List<ProductCategory>> getTree() {
        return CommonResult.success(categoryService.getTree());
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody ProductCategory category) {
        categoryService.create(category);
        return CommonResult.success();
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        categoryService.update(category);
        return CommonResult.success();
    }

    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        categoryService.updateStatus(id, status);
        return CommonResult.success();
    }

    @GetMapping("/check-code")
    public CommonResult<Void> checkCode(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        categoryService.checkCode(code, excludeId);
        return CommonResult.success();
    }
} 