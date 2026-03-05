package com.aspect;

import com.annotation.OperationLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志切面
 * 
 * 【功能说明】
 * 1. 拦截标记了@OperationLog 注解的方法
 * 2. 自动记录操作人、操作时间、操作内容、请求参数等信息
 * 3. 记录方法执行耗时
 * 4. 记录异常信息（如果发生）
 * 
 * 【日志格式】
 * ===== 操作开始 =====
 * 操作人：admin
 * 操作方法：add
 * 操作描述：添加竞赛报名
 * 请求参数：{...}
 * ===== 操作成功 =====
 * 耗时：125ms
 * 
 * @author 毕业设计优化版
 * @date 2026-03-05
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    
    // JSON 序列化工具
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 定义切点：所有标记了@OperationLog 注解的方法
     */
    @Pointcut("@annotation(com.annotation.OperationLog)")
    public void operationLogPointcut() {
    }
    
    /**
     * 环绕通知：在方法执行前后记录日志
     * 
     * @param joinPoint 切面对象
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取操作注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes()).getRequest();
        
        // 获取操作人信息
        String operator = getOperator(request);
        String methodName = method.getName();
        String description = operationLog.value();
        String module = operationLog.module();
        
        // 打印操作开始日志
        log.info("\n===== 操作开始 =====");
        log.info("操作模块：{}", module);
        log.info("操作人：{}", operator);
        log.info("操作方法：{}.{}", joinPoint.getTarget().getClass().getSimpleName(), methodName);
        log.info("操作描述：{}", description);
        log.info("请求 URL: {}", request.getRequestURI());
        log.info("请求方式：{}", request.getMethod());
        
        // 记录请求参数（如果需要）
        if (operationLog.recordParams()) {
            try {
                String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                log.info("请求参数：{}", params);
            } catch (Exception e) {
                log.warn("参数序列化失败：{}", e.getMessage());
            }
        }
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 打印操作成功日志
            log.info("===== 操作成功 =====");
            log.info("耗时：{}ms", duration);
            log.info("====================\n");
            
            return result;
            
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 打印操作失败日志
            log.error("===== 操作失败 =====");
            log.error("耗时：{}ms", duration);
            log.error("异常信息：{}", e.getMessage());
            log.error("异常类型：{}", e.getClass().getName());
            log.error("====================\n");
            
            throw e;
        }
    }
    
    /**
     * 获取操作人用户名
     * 
     * @param request HTTP 请求
     * @return 操作人用户名
     */
    private String getOperator(HttpServletRequest request) {
        // 从 Session 中获取用户名
        String username = (String) request.getSession().getAttribute("username");
        
        if (username != null && !username.trim().isEmpty()) {
            return username;
        }
        
        // 如果 Session 中没有，尝试从 Token 获取
        String token = request.getHeader("Token");
        if (token != null && !token.trim().isEmpty()) {
            // TODO: 解析 Token 获取用户名（可根据实际 Token 实现调整）
            return "Token:" + token.substring(0, Math.min(10, token.length()));
        }
        
        return "匿名用户";
    }
}
