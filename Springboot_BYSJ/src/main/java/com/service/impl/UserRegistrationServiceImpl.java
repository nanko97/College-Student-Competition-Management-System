package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JiaoshiEntity;
import com.entity.UserEntity;
import com.entity.XueshengEntity;
import com.entity.dto.UserRegisterDTO;
import com.entity.vo.RegisterResultVO;
import com.service.JiaoshiService;
import com.service.UserRegistrationService;
import com.service.UserService;
import com.service.XueshengService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户注册服务实现
 */
@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    
    @Autowired
    private XueshengService xueshengService;
    
    @Autowired
    private JiaoshiService jiaoshiService;
    
    @Autowired
    private UserService userService;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResultVO register(UserRegisterDTO dto) {
        // 1. 验证账号唯一性
        if (!validateAccountUnique(dto.getRole(), dto.getAccount())) {
            return RegisterResultVO.error("账号已存在");
        }
        
        // 2. 根据角色创建对应实体（使用雪花算法生成唯一 ID）
        Long userId = com.utils.IdWorker.getId();
        
        switch (dto.getRole()) {
            case "学生":
                return registerStudent(dto, userId);
            case "教师":
                return registerTeacher(dto, userId);
            case "管理员":
                return registerAdmin(dto, userId);
            default:
                return RegisterResultVO.error("不支持的角色类型");
        }
    }
    
    /**
     * 注册学生
     */
    private RegisterResultVO registerStudent(UserRegisterDTO dto, Long userId) {
        // 1. 创建学生记录
        XueshengEntity student = new XueshengEntity<>();
        student.setId(userId);
        student.setXuehao(dto.getAccount());
        student.setMima(passwordEncoder.encode(dto.getPassword())); // BCrypt 加密
        student.setXueshengxingming(dto.getName());
        student.setXingbie(dto.getGender());
        student.setXueyuanmingcheng(dto.getCollege());
        student.setBanji(dto.getGrade());
        student.setShouji(dto.getPhone());
        student.setYouxiang(dto.getEmail());
        student.setZhaopian(dto.getPhoto());
        student.setAddtime(new Date());
        
        xueshengService.insert(student);
        
        // 2. 创建系统用户记录
        UserEntity user = new UserEntity();
        user.setUsername(dto.getAccount());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("学生");
        user.setAddtime(new Date());
        
        userService.insert(user);
        
        return RegisterResultVO.success("注册成功", dto.getAccount(), "学生");
    }
    
    /**
     * 注册教师
     */
    private RegisterResultVO registerTeacher(UserRegisterDTO dto, Long userId) {
        // 1. 创建教师记录
        JiaoshiEntity teacher = new JiaoshiEntity<>();
        teacher.setId(userId);
        teacher.setGonghao(dto.getAccount());
        teacher.setMima(passwordEncoder.encode(dto.getPassword())); // BCrypt 加密
        teacher.setJiaoshixingming(dto.getName());
        teacher.setXingbie(dto.getGender());
        teacher.setXueyuanmingcheng(dto.getCollege());
        teacher.setZhicheng(dto.getGrade());
        teacher.setShouji(dto.getPhone());
        teacher.setYouxiang(dto.getEmail());
        teacher.setZhaopian(dto.getPhoto());
        teacher.setAddtime(new Date());
        
        jiaoshiService.insert(teacher);
        
        // 2. 创建系统用户记录
        UserEntity user = new UserEntity();
        user.setUsername(dto.getAccount());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("教师");
        user.setAddtime(new Date());
        
        userService.insert(user);
        
        return RegisterResultVO.success("注册成功", dto.getAccount(), "教师");
    }
    
    /**
     * 注册管理员
     */
    private RegisterResultVO registerAdmin(UserRegisterDTO dto, Long userId) {
        // 管理员只需创建系统用户记录
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername(dto.getAccount());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("管理员");
        user.setAddtime(new Date());
        
        userService.insert(user);
        
        return RegisterResultVO.success("注册成功", dto.getAccount(), "管理员");
    }
    
    @Override
    public boolean validateAccountUnique(String role, String account) {
        switch (role) {
            case "学生":
                XueshengEntity student = xueshengService.selectOne(
                    new EntityWrapper<XueshengEntity>().eq("xuehao", account)
                );
                return student == null;
                
            case "教师":
                JiaoshiEntity teacher = jiaoshiService.selectOne(
                    new EntityWrapper<JiaoshiEntity>().eq("gonghao", account)
                );
                return teacher == null;
                
            case "管理员":
                UserEntity user = userService.selectOne(
                    new EntityWrapper<UserEntity>().eq("username", account)
                );
                return user == null;
                
            default:
                return false;
        }
    }
}
