package com.scm.service.impl;

import com.scm.entity.SaleReturn;
import com.scm.entity.SaleReturnItem;
import com.scm.mapper.SaleReturnMapper;
import com.scm.service.SaleReturnService;
import com.scm.common.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class SaleReturnServiceImpl implements SaleReturnService {
    
    private static final Logger logger = LoggerFactory.getLogger(SaleReturnServiceImpl.class);
    
    @Autowired
    private SaleReturnMapper saleReturnMapper;
    
    @Override
    public PageData<SaleReturn> getList(String returnNo, String customerName, String status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SaleReturn> list = saleReturnMapper.selectList(returnNo, customerName, status, offset, pageSize);
        int total = saleReturnMapper.selectCount(returnNo, customerName, status);
        return new PageData<>(list, total, pageNum, pageSize);
    }
    
    @Override
    public SaleReturn getById(Long id) {
        return saleReturnMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public boolean create(SaleReturn saleReturn) {
        try {
            // 记录输入参数
            logger.info("Creating sale return with data: {}", saleReturn);
            if(saleReturn.getItems() != null) {
                logger.info("Return items: {}", saleReturn.getItems());
            }
            
            // 生成退货单号 SR + 年月日 + 4位序号
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String prefix = "SR" + dateStr;
            String maxReturnNo = saleReturnMapper.selectMaxReturnNoByPrefix(prefix);
            
            int sequence = 1;
            if (maxReturnNo != null) {
                // 提取序号部分并加1
                sequence = Integer.parseInt(maxReturnNo.substring(maxReturnNo.length() - 4)) + 1;
            }
            
            String returnNo = prefix + String.format("%04d", sequence);
            saleReturn.setReturnNo(returnNo);
            saleReturn.setStatus("pending"); // 设置初始状态为待处理
            
            // 插入退货单主表
            logger.info("Inserting main return with returnNo: {}", returnNo);
            if(saleReturnMapper.insert(saleReturn) <= 0) {
                logger.error("Failed to insert main return");
                return false;
            }
            
            // 插入退货单明细
            if(saleReturn.getItems() != null && !saleReturn.getItems().isEmpty()) {
                for(SaleReturnItem item : saleReturn.getItems()) {
                    logger.info("Inserting return item: {}", item);
                    item.setReturnId(saleReturn.getId());
                    if(saleReturnMapper.insertItem(item) <= 0) {
                        logger.error("Failed to insert return item: {}", item);
                        return false;
                    }
                }
            }
            
            logger.info("Successfully created sale return with id: {}", saleReturn.getId());
            return true;
        } catch (Exception e) {
            logger.error("Error creating sale return", e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public boolean update(SaleReturn saleReturn) {
        return saleReturnMapper.update(saleReturn) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        return saleReturnMapper.updateStatus(id, status) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        // 先删除退货单明细
        saleReturnMapper.deleteItems(id);
        // 再删除退货单
        return saleReturnMapper.delete(id) > 0;
    }
} 