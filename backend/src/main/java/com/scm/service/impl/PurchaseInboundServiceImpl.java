package com.scm.service.impl;

import com.scm.entity.PurchaseInbound;
import com.scm.entity.PurchaseInboundItem;
import com.scm.entity.PurchaseOrder;
import com.scm.mapper.PurchaseInboundMapper;
import com.scm.mapper.PurchaseOrderMapper;
import com.scm.service.PurchaseInboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PurchaseInboundServiceImpl implements PurchaseInboundService {
    
    private static final Logger logger = LoggerFactory.getLogger(PurchaseInboundServiceImpl.class);

    @Autowired
    private PurchaseInboundMapper purchaseInboundMapper;
    
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public List<PurchaseInbound> selectList(String code, String orderCode, String status, Integer offset, Integer limit) {
        logger.info("Fetching inbound list with params: code={}, orderCode={}, status={}, offset={}, limit={}", 
                   code, orderCode, status, offset, limit);
        try {
            List<PurchaseInbound> list = purchaseInboundMapper.selectList(code, orderCode, status, offset, limit);
            logger.info("Found {} inbound records", list != null ? list.size() : 0);
            return list;
        } catch (Exception e) {
            logger.error("Error fetching inbound list", e);
            throw e;
        }
    }

    @Override
    public int selectCount(String code, String orderCode, String status) {
        logger.info("Counting inbounds with params: code={}, orderCode={}, status={}", 
                   code, orderCode, status);
        try {
            int count = purchaseInboundMapper.selectCount(code, orderCode, status);
            logger.info("Total count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error counting inbounds", e);
            throw e;
        }
    }

    @Override
    public PurchaseInbound selectById(Long id) {
        return purchaseInboundMapper.selectById(id);
    }

    private String generateInboundCode() {
        // 生成格式: IN + 年月日 + 6位随机数
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.format("IN%s%06d", dateStr, randomNum);
    }

    @Override
    @Transactional
    public void insert(PurchaseInbound inbound) {
        try {
            logger.info("开始创建入库单");
            // 生成入库单编号
            inbound.setCode(generateInboundCode());
            logger.info("生成入库单编号: {}", inbound.getCode());
            
            // 设置初始状态为待审核
            if (inbound.getStatus() == null) {
                inbound.setStatus("pending");
            }
            
            // 从采购订单获取供应商信息
            PurchaseOrder order = purchaseOrderMapper.selectById(inbound.getOrderId());
            if (order != null) {
                inbound.setSupplierId(order.getSupplierId());
                inbound.setSupplier(order.getSupplier());
                logger.info("设置供应商信息: supplierId={}, supplier={}", order.getSupplierId(), order.getSupplier());
            } else {
                logger.error("采购订单不存在: orderId={}", inbound.getOrderId());
                throw new RuntimeException("采购订单不存在");
            }
            
            // 插入入库单主表
            purchaseInboundMapper.insert(inbound);
            logger.info("入库单主表插入成功: id={}", inbound.getId());
            
            // 插入入库单明细
            if (inbound.getItems() != null && !inbound.getItems().isEmpty()) {
                for (PurchaseInboundItem item : inbound.getItems()) {
                    try {
                        item.setInboundId(inbound.getId());
                        item.setInboundCode(inbound.getCode());
                        purchaseInboundMapper.insertItem(item);
                    } catch (Exception e) {
                        logger.error("插入入库单明细失败: inboundId={}, productId={}", inbound.getId(), item.getProductId(), e);
                        throw new RuntimeException("插入入库单明细失败: " + e.getMessage());
                    }
                }
                logger.info("入库单明细插入成功: count={}", inbound.getItems().size());
            }
            
            logger.info("入库单创建完成: code={}", inbound.getCode());
        } catch (Exception e) {
            logger.error("创建入库单失败", e);
            throw new RuntimeException("创建入库单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void update(PurchaseInbound inbound) {
        purchaseInboundMapper.update(inbound);
        if (inbound.getItems() != null && !inbound.getItems().isEmpty()) {
            purchaseInboundMapper.deleteItemsByInboundId(inbound.getId());
            inbound.getItems().forEach(item -> {
                item.setInboundId(inbound.getId());
                item.setInboundCode(inbound.getCode());
                purchaseInboundMapper.insertItem(item);
            });
        }
    }

    @Override
    public void updateStatus(Long id, String status) {
        purchaseInboundMapper.updateStatus(id, status);
    }
} 