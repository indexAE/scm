package com.scm.service.impl;

import com.scm.entity.User;
import com.scm.mapper.UserMapper;
import com.scm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public User register(String username, String password) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            return null;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        
        // 插入数据库
        userMapper.insert(user);
        
        return user;
    }

    @Override
    public List<User> getUserList() {
        return userMapper.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean updateUser(User user) {
        // 如果要更新用户名，需要检查新用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return false;
        }
        
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        // 验证原密码是否正确
        User user = userMapper.findById(id);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        // 更新新密码
        user.setPassword(newPassword);
        return userMapper.update(user) > 0;
    }
} 