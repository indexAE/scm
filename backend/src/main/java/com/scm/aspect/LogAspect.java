package com.scm.aspect;

import com.scm.service.LogService;
import com.scm.service.SettingsService;
import com.scm.entity.SystemSettings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private SettingsService settingsService;
    
    @Pointcut("execution(* com.scm.controller.*.*(..)) && !execution(* com.scm.controller.LogController.*(..))")
    public void controllerPointcut() {}
    
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        try {
            // 检查操作日志是否启用
            SystemSettings settings = settingsService.getSettings();
            if (settings == null || !settings.getOperationLogEnabled()) {
                return;
            }
            
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 获取用户名
                String username = (String) request.getSession().getAttribute("username");
                if (username == null) {
                    username = "未登录用户";
                }
                
                // 获取请求信息
                String method = request.getMethod();
                String operation = joinPoint.getSignature().getDeclaringTypeName() + "." + 
                                 joinPoint.getSignature().getName();
                String params = Arrays.toString(joinPoint.getArgs());
                String ip = request.getRemoteAddr();
                
                // 记录操作日志
                logService.recordOperationLog(username, operation, method, params, ip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 