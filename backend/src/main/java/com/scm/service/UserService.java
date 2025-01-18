package com.scm.service;

import com.scm.entity.User;
import java.util.List;

public interface UserService {
    User login(String username, String password);
    
    // 注册新用户
    User register(String username, String password);

    // 获取用户列表
    List<User> getUserList();

    // 根据ID获取用户
    User getUserById(Long id);

    // 更新用户信息
    boolean updateUser(User user);

    // 删除用户
    boolean deleteUser(Long id);

    // 修改密码
    boolean updatePassword(Long id, String oldPassword, String newPassword);
} 