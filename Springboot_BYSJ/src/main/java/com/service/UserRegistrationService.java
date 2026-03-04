package com.service;

import com.entity.dto.UserRegisterDTO;
import com.entity.vo.RegisterResultVO;

/**
 * 用户注册服务接口
 */
public interface UserRegistrationService {
    
    /**
     * 统一注册方法
     * @param dto 注册信息
     * @return 注册结果
     */
    RegisterResultVO register(UserRegisterDTO dto);
    
    /**
     * 验证账号唯一性
     * @param role 角色
     * @param account 账号
     * @return true-可用，false-已存在
     */
    boolean validateAccountUnique(String role, String account);
}
