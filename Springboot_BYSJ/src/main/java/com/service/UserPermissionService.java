package com.service;

/**
 * 用户权限服务
 */
public interface UserPermissionService {
    
    /**
     * 检查用户是否有访问某接口的权限
     * @param role 角色
     * @param uri 请求 URI
     * @return true-有权限，false-无权限
     */
    boolean hasPermission(String role, String uri);
}
