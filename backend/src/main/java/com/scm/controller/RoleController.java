package com.scm.controller;

import com.scm.entity.Role;
import com.scm.service.RoleService;
import com.scm.common.Result;
import com.scm.common.PageData;
import com.scm.common.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public Result getRoles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Boolean status) {
        try {
            PageData<Role> pageData = roleService.getRoles(page, size, roleName, roleCode, status);
            return Result.success(pageData);
        } catch (Exception e) {
            logger.error("获取角色列表失败", e);
            return Result.error("获取角色列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result getRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id);
            if (role != null) {
                return Result.success(role);
            } else {
                return Result.error("角色不存在");
            }
        } catch (Exception e) {
            logger.error("获取角色失败", e);
            return Result.error("获取角色失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result addRole(@RequestBody Role role) {
        try {
            // 检查角色编码是否已存在
            if (roleService.isRoleCodeExists(role.getRoleCode())) {
                return Result.error("角色编码已存在");
            }
            
            // 添加角色
            if (roleService.addRole(role)) {
                return Result.success();
            } else {
                return Result.error("添加角色失败");
            }
        } catch (Exception e) {
            logger.error("添加角色失败", e);
            return Result.error("添加角色失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result updateRole(@PathVariable Long id, @RequestBody Role role) {
        try {
            role.setId(id);
            
            // 检查角色编码是否已存在(排除自身)
            if (roleService.isRoleCodeExistsExcludeSelf(role.getRoleCode(), id)) {
                return Result.error("角色编码已存在");
            }
            
            // 更新角色
            if (roleService.updateRole(role)) {
                return Result.success();
            } else {
                return Result.error("更新角色失败");
            }
        } catch (Exception e) {
            logger.error("更新角色失败", e);
            return Result.error("更新角色失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteRole(@PathVariable Long id) {
        try {
            // 检查角色是否被用户使用
            if (roleService.isRoleInUse(id)) {
                return Result.error("该角色已被用户使用,无法删除");
            }
            
            // 删除角色
            if (roleService.deleteRole(id)) {
                return Result.success();
            } else {
                return Result.error("删除角色失败");
            }
        } catch (Exception e) {
            logger.error("删除角色失败", e);
            return Result.error("删除角色失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result getRolesByUserId(@PathVariable Long userId) {
        try {
            List<Role> roles = roleService.getRolesByUserId(userId);
            return Result.success(roles);
        } catch (Exception e) {
            logger.error("获取用户角色失败", e);
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }

    @PostMapping("/assign")
    public Result assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            if (roleService.assignRole(userId, roleId)) {
                return Result.success();
            } else {
                return Result.error("分配角色失败");
            }
        } catch (Exception e) {
            logger.error("分配角色失败", e);
            return Result.error("分配角色失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public Result removeRole(@RequestParam Long userId, @RequestParam Long roleId) {
        try {
            if (roleService.removeRole(userId, roleId)) {
                return Result.success();
            } else {
                return Result.error("移除角色失败");
            }
        } catch (Exception e) {
            logger.error("移除角色失败", e);
            return Result.error("移除角色失败: " + e.getMessage());
        }
    }
} 