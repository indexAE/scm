package com.scm.service.impl;

import com.scm.entity.ProductCategory;
import com.scm.mapper.ProductCategoryMapper;
import com.scm.service.ProductCategoryService;
import com.scm.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper categoryMapper;

    public ProductCategoryServiceImpl(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public ProductCategory getById(Long id) {
        return categoryMapper.getById(id);
    }

    @Override
    public List<ProductCategory> search(String name, String status) {
        return categoryMapper.search(name, status);
    }

    @Override
    public List<ProductCategory> getTree() {
        // 获取所有分类
        List<ProductCategory> allCategories = categoryMapper.search(null, null);
        if (allCategories == null || allCategories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 构建父分类ID到分类的映射
        Map<Long, ProductCategory> idMap = allCategories.stream()
                .collect(Collectors.toMap(ProductCategory::getId, category -> category));
        
        // 构建子分类映射
        Map<Long, List<ProductCategory>> childrenMap = new HashMap<>();
        
        // 处理每个分类
        for (ProductCategory category : allCategories) {
            Long parentId = category.getParentId();
            if (parentId != null) {
                // 检查是否自引用
                if (parentId.equals(category.getId())) {
                    // 如果是自引用,将其视为顶级分类
                    category.setParentId(null);
                    continue;
                }
                
                // 如果父分类存在，将当前分类添加到父分类的子列表中
                if (idMap.containsKey(parentId)) {
                    childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>())
                            .add(category);
                } else {
                    // 如果父分类不存在，将其视为顶级分类
                    category.setParentId(null);
                }
            }
        }
        
        // 设置每个分类的子分类列表
        allCategories.forEach(category -> 
            category.setChildren(childrenMap.getOrDefault(category.getId(), new ArrayList<>())));
        
        // 返回顶级分类（包括原本就是顶级的和父分类不存在的）
        return allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());
    }

    private List<ProductCategory> buildChildren(Long parentId, Map<Long, List<ProductCategory>> childrenMap) {
        return childrenMap.getOrDefault(parentId, new ArrayList<>());
    }

    @Override
    @Transactional
    public void create(ProductCategory category) {
        // 检查分类编码是否重复
        checkCode(category.getCode(), null);
        
        // 设置初始状态
        category.setStatus("enabled");
        
        if (categoryMapper.insert(category) != 1) {
            throw new BusinessException("创建分类失败");
        }
    }

    @Override
    @Transactional
    public void update(ProductCategory category) {
        // 检查分类编码是否重复
        checkCode(category.getCode(), category.getId());
        
        // 检查是否将分类设置为自己的子分类
        if (isChildCategory(category.getId(), category.getParentId())) {
            throw new BusinessException("不能将分类设置为自己的子分类");
        }
        
        if (categoryMapper.update(category) != 1) {
            throw new BusinessException("更新分类失败");
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        ProductCategory category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        if (categoryMapper.updateStatus(id, status, category.getUpdateBy()) != 1) {
            throw new BusinessException("更新分类状态失败");
        }
        
        // 如果是禁用操作，同时禁用所有子分类
        if ("disabled".equals(status)) {
            disableChildren(id);
        }
    }

    private void disableChildren(Long parentId) {
        List<ProductCategory> children = getChildren(parentId);
        for (ProductCategory child : children) {
            categoryMapper.updateStatus(child.getId(), "disabled", child.getUpdateBy());
            disableChildren(child.getId());
        }
    }

    @Override
    public void checkCode(String code, Long excludeId) {
        ProductCategory existingCategory = categoryMapper.getByCode(code, excludeId);
        if (existingCategory != null) {
            throw new BusinessException("分类编码已存在");
        }
    }

    @Override
    public List<ProductCategory> getChildren(Long parentId) {
        return categoryMapper.getByParentId(parentId);
    }

    private boolean isChildCategory(Long categoryId, Long parentId) {
        if (parentId == null || categoryId == null) {
            return false;
        }

        // 检查当前节点
        if (categoryId.equals(parentId)) {
            return true;
        }

        // 获取所有分类
        List<ProductCategory> allCategories = categoryMapper.search(null, null);
        if (allCategories == null || allCategories.isEmpty()) {
            return false;
        }

        // 构建父分类ID到分类的映射
        Map<Long, ProductCategory> idMap = allCategories.stream()
                .collect(Collectors.toMap(ProductCategory::getId, category -> category));

        // 从父分类开始向上遍历所有祖先分类
        Long currentId = parentId;
        Set<Long> visitedIds = new HashSet<>();
        
        while (currentId != null) {
            // 检查是否已经访问过该节点(避免死循环)
            if (!visitedIds.add(currentId)) {
                return false;
            }
            
            // 检查当前节点是否为目标分类
            if (categoryId.equals(currentId)) {
                return true;
            }
            
            // 获取父节点
            ProductCategory current = idMap.get(currentId);
            if (current == null) {
                return false;
            }
            
            // 移动到父节点
            currentId = current.getParentId();
        }
        
        return false;
    }
} 