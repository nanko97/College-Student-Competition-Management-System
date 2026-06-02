package com.aspect;

import com.annotation.OperationLog;
import com.entity.OperationLogEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志切面
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    
    @Autowired
    private OperationLogService operationLogService;
    
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
        String operatorRole = getOperatorRole(request);
        String methodName = method.getName();
        String description = operationLog.value();
        String module = operationLog.module();
        
        // 创建操作日志实体
        OperationLogEntity logEntity = new OperationLogEntity();
        logEntity.setOperator(operator);
        logEntity.setOperatorRole(operatorRole);
        logEntity.setOperationModule(module);
        logEntity.setOperationType(getOperationType(methodName));
        logEntity.setOperationDesc(description);
        logEntity.setRequestMethod(request.getMethod());
        logEntity.setRequestUrl(request.getRequestURI());
        logEntity.setClientIp(getClientIp(request));
        logEntity.setUserAgent(request.getHeader("User-Agent"));
        
        // 记录请求参数（如果需要）
        if (operationLog.recordParams()) {
            try {
                String params = objectMapper.writeValueAsString(joinPoint.getArgs());
                logEntity.setRequestParams(params.length() > 2000 ? params.substring(0, 2000) + "..." : params);
            } catch (Exception e) {
                log.warn("参数序列化失败：{}", e.getMessage());
                logEntity.setRequestParams("参数序列化失败");
            }
        }
        
        // 打印操作开始日志
        log.info("\n===== 操作开始 =====");
        log.info("操作模块：{}", module);
        log.info("操作人：{}", operator);
        log.info("操作方法：{}.{}", joinPoint.getTarget().getClass().getSimpleName(), methodName);
        log.info("操作描述：{}", description);
        log.info("请求 URL: {}", request.getRequestURI());
        log.info("请求方式：{}", request.getMethod());
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 设置成功状态
            logEntity.setStatus("成功");
            logEntity.setExecutionTime(duration);
            logEntity.setCreateTime(new Date());
            
            // 异步保存日志到数据库
            saveLogAsync(logEntity);
            
            // 打印操作成功日志
            log.info("===== 操作成功 =====");
            log.info("耗时：{}ms", duration);
            log.info("====================\n");
            
            return result;
            
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 设置失败状态
            logEntity.setStatus("失败");
            logEntity.setExecutionTime(duration);
            logEntity.setErrorMsg(e.getMessage());
            logEntity.setCreateTime(new Date());
            
            // 异步保存日志到数据库
            saveLogAsync(logEntity);
            
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
            return "Token:" + token.substring(0, Math.min(10, token.length()));
        }
        
        return "匿名用户";
    }
    
    /**
     * 获取操作人角色
     * 
     * @param request HTTP 请求
     * @return 操作人角色
     */
    private String getOperatorRole(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("role");
        if (role != null && !role.trim().isEmpty()) {
            return role;
        }
        return "未知";
    }
    
    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP 请求
     * @return 客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    /**
     * 根据方法名判断操作类型
     * 
     * @param methodName 方法名
     * @return 操作类型
     */
    private String getOperationType(String methodName) {
        if (methodName.contains("add") || methodName.contains("save") || methodName.contains("insert")) {
            return "新增";
        } else if (methodName.contains("update") || methodName.contains("edit")) {
            return "修改";
        } else if (methodName.contains("delete") || methodName.contains("remove")) {
            return "删除";
        } else if (methodName.contains("shenhe") || methodName.contains("approve")) {
            return "审核";
        } else if (methodName.contains("query") || methodName.contains("list") || methodName.contains("page")) {
            return "查询";
        } else {
            return "其他";
        }
    }
    
    /**
     * 异步保存日志到数据库
     * 
     * @param logEntity 日志实体
     */
    private void saveLogAsync(final OperationLogEntity logEntity) {
        // 使用新线程异步保存，避免影响主业务流程
        new Thread(() -> {
            try {
                operationLogService.saveLog(logEntity);
            } catch (Exception e) {
                log.error("异步保存操作日志失败", e);
            }
        }).start();
    }
}
