package com.scm.controller;

import com.scm.entity.User;
import com.scm.service.UserService;
import com.scm.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LogService logService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        User dbUser = userService.login(user.getUsername(), user.getPassword());
        
        // 获取客户端信息
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String browser = getBrowser(userAgent);
        String os = getOS(userAgent);
        
        if (dbUser != null) {
            // 记录登录成功日志
            logService.recordLoginLog(user.getUsername(), ip, true, browser, os);
            
            result.put("code", 0);
            result.put("message", "登录成功");
            result.put("data", dbUser);
        } else {
            // 记录登录失败日志
            logService.recordLoginLog(user.getUsername(), ip, false, browser, os);
            
            result.put("code", 1);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    // 获取浏览器信息
    private String getBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("safari")) {
            return "Safari";
        } else if (userAgent.contains("edge")) {
            return "Edge";
        } else if (userAgent.contains("msie") || userAgent.contains("trident")) {
            return "IE";
        }
        return "Unknown";
    }

    // 获取操作系统信息
    private String getOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        
        if (userAgent.contains("windows")) {
            return "Windows";
        } else if (userAgent.contains("mac")) {
            return "MacOS";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("iphone") || userAgent.contains("ipad")) {
            return "iOS";
        }
        return "Unknown";
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 参数验证
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            result.put("code", 1);
            result.put("message", "用户名和密码不能为空");
            return result;
        }
        
        User newUser = userService.register(user.getUsername(), user.getPassword());
        if (newUser != null) {
            result.put("code", 0);
            result.put("message", "注册成功");
            result.put("data", newUser);
        } else {
            result.put("code", 1);
            result.put("message", "用户名已存在");
        }
        return result;
    }

    @GetMapping("/users")
    public Map<String, Object> getUserList() {
        Map<String, Object> result = new HashMap<>();
        
        List<User> users = userService.getUserList();
        result.put("code", 0);
        result.put("message", "获取成功");
        result.put("data", users);
        
        return result;
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userService.getUserById(id);
        if (user != null) {
            result.put("code", 0);
            result.put("message", "获取成功");
            result.put("data", user);
        } else {
            result.put("code", 1);
            result.put("message", "用户不存在");
        }
        
        return result;
    }

    @PutMapping("/users/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        user.setId(id);
        if (userService.updateUser(user)) {
            result.put("code", 0);
            result.put("message", "更新成功");
        } else {
            result.put("code", 1);
            result.put("message", "更新失败");
        }
        
        return result;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        if (userService.deleteUser(id)) {
            result.put("code", 0);
            result.put("message", "删除成功");
        } else {
            result.put("code", 1);
            result.put("message", "删除失败");
        }
        
        return result;
    }

    @PutMapping("/users/{id}/password")
    public Map<String, Object> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (userService.updatePassword(id, oldPassword, newPassword)) {
            result.put("code", 0);
            result.put("message", "密码修改成功");
        } else {
            result.put("code", 1);
            result.put("message", "原密码错误");
        }
        
        return result;
    }
} 