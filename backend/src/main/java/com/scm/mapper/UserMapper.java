package com.scm.mapper;

import com.scm.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    // 根据用户名查找用户
    User findByUsername(@Param("username") String username);
    
    // 插入新用户
    int insert(User user);

    // 获取所有用户
    List<User> findAll();

    // 根据ID查找用户
    User findById(@Param("id") Long id);

    // 更新用户信息
    int update(User user);

    // 根据ID删除用户
    int deleteById(@Param("id") Long id);
} 