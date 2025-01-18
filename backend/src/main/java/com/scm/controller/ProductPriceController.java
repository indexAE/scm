package com.scm.controller;

import com.scm.common.api.CommonResult;
import com.scm.common.api.PageResult;
import com.scm.entity.ProductPrice;
import com.scm.service.ProductPriceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/prices")
public class ProductPriceController {

    private final ProductPriceService priceService;

    public ProductPriceController(ProductPriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/{id}")
    public CommonResult<ProductPrice> getById(@PathVariable Long id) {
        return CommonResult.success(priceService.getById(id));
    }

    @GetMapping
    public CommonResult<PageResult<ProductPrice>> search(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return CommonResult.success(priceService.search(productName, categoryId, pageNum, pageSize));
    }

    @PostMapping
    public CommonResult<Void> create(@RequestBody ProductPrice price) {
        priceService.create(price);
        return CommonResult.success();
    }

    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody ProductPrice price) {
        price.setId(id);
        priceService.update(price);
        return CommonResult.success();
    }

    @GetMapping("/{productId}/history")
    public CommonResult<List<ProductPrice>> getHistory(@PathVariable Long productId) {
        return CommonResult.success(priceService.getHistoryByProductId(productId));
    }

    @GetMapping("/{productId}/latest")
    public CommonResult<ProductPrice> getLatest(@PathVariable Long productId) {
        return CommonResult.success(priceService.getLatestByProductId(productId));
    }
} 