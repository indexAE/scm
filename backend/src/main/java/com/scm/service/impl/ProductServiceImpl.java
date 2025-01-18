package com.scm.service.impl;

import com.scm.entity.Product;
import com.scm.mapper.ProductMapper;
import com.scm.service.ProductService;
import com.scm.common.exception.BusinessException;
import com.scm.common.api.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public Product getById(Long id) {
        return productMapper.getById(id);
    }

    @Override
    public PageResult<Product> search(String name, String code, Long categoryId, String status, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        long total = productMapper.count(name, code, categoryId, status);
        
        // 查询列表
        List<Product> list = productMapper.search(name, code, categoryId, status, offset, pageSize);
        
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional
    public void create(Product product) {
        // 检查商品编码是否重复
        checkCode(product.getCode(), null);
        
        // 设置初始状态
        product.setStatus("enabled");
        product.setStock(0);
        
        if (productMapper.insert(product) != 1) {
            throw new BusinessException("创建商品失败");
        }
    }

    @Override
    @Transactional
    public void update(Product product) {
        // 检查商品编码是否重复
        checkCode(product.getCode(), product.getId());
        
        if (productMapper.update(product) != 1) {
            throw new BusinessException("更新商品失败");
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (productMapper.updateStatus(id, status, product.getUpdateBy()) != 1) {
            throw new BusinessException("更新商品状态失败");
        }
    }

    @Override
    public void checkCode(String code, Long excludeId) {
        Product existingProduct = productMapper.getByCode(code, excludeId);
        if (existingProduct != null) {
            throw new BusinessException("商品编码已存在");
        }
    }
} 