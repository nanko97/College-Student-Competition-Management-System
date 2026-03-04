package com.entity.vo;

import java.io.Serializable;

/**
 * 注册结果视图对象
 */
public class RegisterResultVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer code;      // 0-成功，其他 - 失败
    private String msg;        // 消息
    private String account;    // 账号
    private String role;       // 角色
    
    public RegisterResultVO() {
    }
    
    public static RegisterResultVO success(String msg, String account, String role) {
        RegisterResultVO vo = new RegisterResultVO();
        vo.setCode(0);
        vo.setMsg(msg);
        vo.setAccount(account);
        vo.setRole(role);
        return vo;
    }
    
    public static RegisterResultVO error(String msg) {
        RegisterResultVO vo = new RegisterResultVO();
        vo.setCode(1);
        vo.setMsg(msg);
        return vo;
    }
    
    // Getters and Setters
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "RegisterResultVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", account='" + account + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
