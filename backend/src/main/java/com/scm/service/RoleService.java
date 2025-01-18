package com.scm.service;

import com.scm.entity.Role;
import com.scm.common.PageData;
import java.util.List;

public interface RoleService {
    // 分页查询角色列表
    PageData<Role> getRoles(Integer page, Integer size, String roleName, String roleCode, Boolean status);
    
    // 根据ID获取角色
    Role getRoleById(Long id);
    
    // 添加角色
    boolean addRole(Role role);
    
    // 更新角色
    boolean updateRole(Role role);
    
    // 删除角色
    boolean deleteRole(Long id);
    
    // 根据用户ID获取角色列表
    List<Role> getRolesByUserId(Long userId);
    
    // 为用户分配角色
    boolean assignRole(Long userId, Long roleId);
    
    // 移除用户角色
    boolean removeRole(Long userId, Long roleId);
    
    // 检查角色编码是否存在
    boolean isRoleCodeExists(String roleCode);
    
    // 检查角色编码是否存在(排除自身)
    boolean isRoleCodeExistsExcludeSelf(String roleCode, Long id);
    
    // 检查角色是否被用户使用
    boolean isRoleInUse(Long id);
} 