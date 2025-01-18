package com.scm.service.impl;

import com.scm.entity.Role;
import com.scm.mapper.RoleMapper;
import com.scm.service.RoleService;
import com.scm.common.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public PageData<Role> getRoles(Integer page, Integer size, String roleName, String roleCode, Boolean status) {
        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 查询数据
        List<Role> list = roleMapper.getRoles(offset, size, roleName, roleCode, status);
        long total = roleMapper.getRolesCount(roleName, roleCode, status);
        
        return new PageData<>(list, total, page, size);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.getRoleById(id);
    }

    @Override
    public boolean addRole(Role role) {
        return roleMapper.addRole(role) > 0;
    }

    @Override
    public boolean updateRole(Role role) {
        return roleMapper.updateRole(role) > 0;
    }

    @Override
    public boolean deleteRole(Long id) {
        return roleMapper.deleteRole(id) > 0;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.getRolesByUserId(userId);
    }

    @Override
    public boolean assignRole(Long userId, Long roleId) {
        return roleMapper.assignRole(userId, roleId) > 0;
    }

    @Override
    public boolean removeRole(Long userId, Long roleId) {
        return roleMapper.removeRole(userId, roleId) > 0;
    }

    @Override
    public boolean isRoleCodeExists(String roleCode) {
        return roleMapper.countByRoleCode(roleCode) > 0;
    }

    @Override
    public boolean isRoleCodeExistsExcludeSelf(String roleCode, Long id) {
        return roleMapper.countByRoleCodeExcludeId(roleCode, id) > 0;
    }

    @Override
    public boolean isRoleInUse(Long id) {
        return roleMapper.countUsersByRoleId(id) > 0;
    }
} 