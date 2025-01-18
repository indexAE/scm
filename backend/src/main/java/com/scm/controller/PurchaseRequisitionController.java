package com.scm.controller;

import com.scm.common.BaseController;
import com.scm.common.Result;
import com.scm.entity.PurchaseRequisition;
import com.scm.service.PurchaseRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase/requisitions")
public class PurchaseRequisitionController extends BaseController {

    @Autowired
    private PurchaseRequisitionService purchaseRequisitionService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<PurchaseRequisition> list = purchaseRequisitionService.selectList(code, status, offset, limit);
        int total = purchaseRequisitionService.selectCount(code, status);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<PurchaseRequisition> getById(@PathVariable Long id) {
        PurchaseRequisition requisition = purchaseRequisitionService.selectById(id);
        return Result.success(requisition);
    }

    @PostMapping
    public Result<PurchaseRequisition> add(@RequestBody PurchaseRequisition requisition) {
        purchaseRequisitionService.insert(requisition);
        return Result.success(requisition);
    }

    @PutMapping("/{id}")
    public Result<PurchaseRequisition> update(@PathVariable Long id, @RequestBody PurchaseRequisition requisition) {
        requisition.setId(id);
        purchaseRequisitionService.update(requisition);
        return Result.success(requisition);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        purchaseRequisitionService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String remark = body.get("remark");
        purchaseRequisitionService.updateStatus(id, status, remark);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String action = body.get("action"); // approve or reject
        String remark = body.get("remark"); // approval remark
        
        String status = "approved".equals(action) ? "approved" : "rejected";
        purchaseRequisitionService.updateStatus(id, status, remark);
        return Result.success();
    }
} 