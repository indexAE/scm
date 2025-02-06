package com.scm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scm.common.Result;
import com.scm.entity.Carrier;
import com.scm.service.CarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logistics/carriers")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    /**
     * 分页查询承运商列表
     */
    @GetMapping
    public Result<IPage<Carrier>> getCarrierPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) String status) {
        IPage<Carrier> carrierPage = carrierService.getCarrierPage(page, limit, name, contact, status);
        return Result.success(carrierPage);
    }

    /**
     * 获取所有承运商（不分页）
     */
    @GetMapping("/all")
    public Result<List<Carrier>> getAllCarriers() {
        List<Carrier> carriers = carrierService.list();
        return Result.success(carriers);
    }

    /**
     * 获取承运商详情
     */
    @GetMapping("/{id}")
    public Result<Carrier> getCarrierById(@PathVariable Long id) {
        Carrier carrier = carrierService.getById(id);
        return Result.success(carrier);
    }

    /**
     * 新增承运商
     */
    @PostMapping
    public Result<?> createCarrier(@RequestBody Carrier carrier) {
        carrierService.save(carrier);
        return Result.success();
    }

    /**
     * 更新承运商
     */
    @PutMapping("/{id}")
    public Result<?> updateCarrier(@PathVariable Long id, @RequestBody Carrier carrier) {
        carrier.setId(id);
        carrierService.updateById(carrier);
        return Result.success();
    }

    /**
     * 启用承运商
     */
    @PutMapping("/{id}/enable")
    public Result<?> enableCarrier(@PathVariable Long id) {
        carrierService.enableCarrier(id);
        return Result.success();
    }

    /**
     * 禁用承运商
     */
    @PutMapping("/{id}/disable")
    public Result<?> disableCarrier(@PathVariable Long id) {
        carrierService.disableCarrier(id);
        return Result.success();
    }
} 