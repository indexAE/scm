package com.scm.service.impl;

import com.scm.entity.ProductPrice;
import com.scm.mapper.ProductPriceMapper;
import com.scm.service.ProductPriceService;
import com.scm.common.exception.BusinessException;
import com.scm.common.api.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceMapper priceMapper;

    public ProductPriceServiceImpl(ProductPriceMapper priceMapper) {
        this.priceMapper = priceMapper;
    }

    @Override
    public ProductPrice getById(Long id) {
        return priceMapper.getById(id);
    }

    @Override
    public PageResult<ProductPrice> search(String productName, Long categoryId, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        long total = priceMapper.count(productName, categoryId);
        
        // 查询列表
        List<ProductPrice> list = priceMapper.search(productName, categoryId, offset, pageSize);
        
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional
    public void create(ProductPrice price) {
        if (priceMapper.insert(price) != 1) {
            throw new BusinessException("创建价格记录失败");
        }
    }

    @Override
    @Transactional
    public void update(ProductPrice price) {
        if (priceMapper.update(price) != 1) {
            throw new BusinessException("更新价格记录失败");
        }
    }

    @Override
    public List<ProductPrice> getHistoryByProductId(Long productId) {
        return priceMapper.getHistoryByProductId(productId);
    }

    @Override
    public ProductPrice getLatestByProductId(Long productId) {
        return priceMapper.getLatestByProductId(productId);
    }
} 