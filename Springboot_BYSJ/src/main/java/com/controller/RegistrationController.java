package com.controller;

import com.annotation.IgnoreAuth;
import com.entity.dto.UserRegisterDTO;
import com.entity.vo.RegisterResultVO;
import com.service.UserRegistrationService;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 统一用户注册控制器
 */
@Slf4j
@RestController
@RequestMapping("/registration")
public class RegistrationController {
    
    @Autowired
    private UserRegistrationService registrationService;
    
    /**
     * 统一注册接口
     */
    @PostMapping("/register")
    @IgnoreAuth
    public R register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("收到注册请求：role={}, account={}, name={}", dto.getRole(), dto.getAccount(), dto.getName());
        
        RegisterResultVO result = registrationService.register(dto);
        
        if (result.getCode() == 0) {
            log.info("注册成功：{}", dto.getAccount());
            return R.ok().put("data", result);
        } else {
            log.error("注册失败：{} - {}", dto.getAccount(), result.getMsg());
            return R.error(result.getMsg());
        }
    }
    
    /**
     * 检查账号是否可用
     */
    @GetMapping("/check-account")
    @IgnoreAuth
    public R checkAccount(@RequestParam String role, @RequestParam String account) {
        boolean available = registrationService.validateAccountUnique(role, account);
        return R.ok().put("available", available);
    }
}
