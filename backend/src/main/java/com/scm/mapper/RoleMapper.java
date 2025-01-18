package com.scm.mapper;

import com.scm.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    // 分页查询角色列表
    List<Role> getRoles(@Param("offset") Integer offset, 
                       @Param("size") Integer size, 
                       @Param("roleName") String roleName, 
                       @Param("roleCode") String roleCode, 
                       @Param("status") Boolean status);
    
    // 查询角色总数
    long getRolesCount(@Param("roleName") String roleName, 
                      @Param("roleCode") String roleCode, 
                      @Param("status") Boolean status);
    
    // 根据ID获取角色
    Role getRoleById(@Param("id") Long id);
    
    // 添加角色
    int addRole(Role role);
    
    // 更新角色
    int updateRole(Role role);
    
    // 删除角色
    int deleteRole(@Param("id") Long id);
    
    // 根据用户ID获取角色列表
    List<Role> getRolesByUserId(@Param("userId") Long userId);
    
    // 为用户分配角色
    int assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    // 移除用户角色
    int removeRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    // 根据角色编码统计数量
    int countByRoleCode(@Param("roleCode") String roleCode);
    
    // 根据角色编码统计数量(排除自身)
    int countByRoleCodeExcludeId(@Param("roleCode") String roleCode, @Param("id") Long id);
    
    // 统计使用该角色的用户数量
    int countUsersByRoleId(@Param("id") Long id);
} 