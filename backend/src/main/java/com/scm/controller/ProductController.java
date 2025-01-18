package com.scm.controller;

import com.scm.common.api.CommonResult;
import com.scm.common.api.PageResult;
import com.scm.entity.Product;
import com.scm.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public CommonResult<Product> getById(@PathVariable Long id) {
        return CommonResult.success(productService.getById(id));
    }

    @GetMapping
    public CommonResult<PageResult<Product>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(productService.search(name, code, categoryId, status, pageNum, pageSize));
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody Product product) {
        productService.create(product);
        return CommonResult.success();
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.update(product);
        return CommonResult.success();
    }

    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        productService.updateStatus(id, status);
        return CommonResult.success();
    }

    @GetMapping("/check-code")
    public CommonResult<Void> checkCode(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        productService.checkCode(code, excludeId);
        return CommonResult.success();
    }
} 