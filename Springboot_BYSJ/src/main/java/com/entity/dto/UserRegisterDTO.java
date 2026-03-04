package com.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 统一用户注册数据传输对象
 */
public class UserRegisterDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号格式不正确 (4-20 位字母数字下划线)")
    private String account;  // 学号/工号/管理员账号
    
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_@#$%^&+=!]{6,20}$", message = "密码格式不正确 (6-20 位)")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(学生 | 教师 | 管理员)$", message = "角色必须是学生、教师或管理员")
    private String role;
    
    private String email;
    private String phone;
    private String gender;
    private String college;
    private String grade;  // 班级或职称
    private String photo;
    
    public UserRegisterDTO() {
    }
    
    // Getters and Setters
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getCollege() {
        return college;
    }
    
    public void setCollege(String college) {
        this.college = college;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public String getPhoto() {
        return photo;
    }
    
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    
    @Override
    public String toString() {
        return "UserRegisterDTO{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", college='" + college + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
}
