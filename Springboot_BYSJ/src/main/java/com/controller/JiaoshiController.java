package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JiaoshiEntity;
import com.entity.TokenEntity;
import com.entity.view.JiaoshiView;
import com.service.DataSyncService;
import com.service.JiaoshiService;
import com.service.TokenService;
import com.utils.IdWorker;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.PasswordValidator;
import com.utils.R;
import com.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 教师信息管理控制器
 * 功能：教师登录/注册、信息管理、查询统计
 * 
 * @author毕业设计优化版
 * @date 2026-03-05
 * 
 * 优化说明：
 * 1. 添加详细的中文注释，便于理解和答辩
 * 2. 增加参数校验，提升数据安全性
 * 3. 添加异常处理和日志记录，便于问题排查
 * 4. 统一返回结果格式，提升用户体验
 */
@RestController
@RequestMapping("/jiaoshi")
@Slf4j // 日志注解，便于问题排查
public class JiaoshiController {
    
    @Autowired
    private JiaoshiService jiaoshiService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt 密码加密器
    
    @Autowired
    private DataSyncService dataSyncService; // 数据联动同步服务

    /**
     * 教师登录接口
     * 功能：验证工号和密码，生成登录 Token
     * 
     * 认证流程：
     * 1. 根据工号查询教师信息
     * 2. 使用 BCrypt 验证密码
     * 3. 生成 Token 并返回
     * 
     * @param username 工号 (用户名)
     * @param password 密码
     * @param captcha 验证码 (暂未启用)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含 Token
     */
    @IgnoreAuth // 忽略权限验证，允许未登录访问
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        try {
            // 1. 基础参数校验
            if (!StringUtils.hasText(username)) {
                log.warn("教师登录失败：工号为空");
                return R.error("工号不能为空");
            }
            
            if (!StringUtils.hasText(password)) {
                log.warn("教师登录失败：密码为空");
                return R.error("密码不能为空");
            }
            
            // 2. 根据工号查询教师
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", username);
            JiaoshiEntity user = jiaoshiService.selectOne(queryWrapper);
            
            // 3. 用户不存在或密码错误
            if (user == null || !passwordEncoder.matches(password, user.getMima())) {
                log.warn("教师登录失败：工号{}账号或密码不正确", username);
                return R.error("账号或密码不正确");
            }
            
            // 4. 登录成功，生成 Token
            String token = tokenService.generateToken(user.getId(), username, "jiaoshi", "教师");
            log.info("教师 {} 登录成功", username);
            // 返回token和教师姓名
            return R.ok()
                    .put("token", token)
                    .put("jiaoshixingming", user.getJiaoshixingming());
            
        } catch (Exception e) {
            log.error("教师登录异常：", e);
            return R.error("登录失败，请稍后重试");
        }
    }

    /**
     * 教师注册接口
     * 功能：新教师注册账号
     * 
     * 注册流程：
     * 1. 验证工号和密码
     * 2. 检查密码强度
     * 3. 检查工号是否已存在
     * 4. 密码加密存储
     * 5. 保存到数据库
     * 
     * @param jiaoshi 教师实体 (包含工号、密码等信息)
     * @return R 统一返回结果
     */
    @IgnoreAuth // 忽略权限验证，允许未登录访问
    @RequestMapping("/register")
    public R register(@RequestBody JiaoshiEntity jiaoshi) {
        try {
            // 1. 基础参数校验
            if (!StringUtils.hasText(jiaoshi.getGonghao()) || !StringUtils.hasText(jiaoshi.getMima())) {
                log.warn("教师注册失败：工号和密码不能为空");
                return R.error("工号和密码不能为空");
            }
            
            // 账号格式校验：只能是字母、数字、下划线，长度7-20位
            if (!jiaoshi.getGonghao().matches("^[a-zA-Z0-9_]{7,20}$")) {
                log.warn("教师注册失败：账号格式不正确，{}", jiaoshi.getGonghao());
                return R.error("账号格式不正确，只能包含字母、数字和下划线，长度7-20位");
            }
            
            // 2. 密码强度校验
            String passwordError = PasswordValidator.validateAndGetError(jiaoshi.getMima());
            if (passwordError != null) {
                log.warn("教师注册失败：{}", passwordError);
                return R.error(passwordError);
            }
            
            // 3. 检查账号是否存在
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", jiaoshi.getGonghao());
            JiaoshiEntity user = jiaoshiService.selectOne(queryWrapper);
            if (user != null) {
                log.warn("教师注册失败：工号{}已存在", jiaoshi.getGonghao());
                return R.error("注册用户已存在");
            }
            
            // 4. 密码加密 (使用 BCrypt 加密)
            jiaoshi.setMima(passwordEncoder.encode(jiaoshi.getMima()));
            
            // 5. 生成 ID 并补充默认值
            Long uId = IdWorker.getId();
            jiaoshi.setId(uId);
            jiaoshi.setAddtime(new Date());
            
            // 6. 保存用户
            jiaoshiService.insert(jiaoshi);
            log.info("教师 {} 注册成功", jiaoshi.getGonghao());
            return R.ok("注册成功");
            
        } catch (Exception e) {
            log.error("教师注册异常：", e);
            return R.error("注册失败，请联系管理员");
        }
    }

    /**
     * 退出登录
     * 功能：清除 Session 会话
     * 
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @RequestMapping("/logout")
    public R logout(HttpServletRequest request) {
        try {
            // 清除 Session
            request.getSession().invalidate();
            log.info("教师退出登录成功");
            return R.ok("退出成功");
        } catch (Exception e) {
            log.error("教师退出登录异常：", e);
            return R.error("退出失败，请重试");
        }
    }

    /**
     * 获取当前登录教师信息
     * 功能：从 Token 中获取用户 ID，查询详细信息
     * 
     * 支持两种认证方式：
     * 1. Token 认证（前端主要使用）
     * 2. Session 认证（兼容旧版）
     * 
     * @param request HTTP 请求 (包含 Token 或 Session)
     * @return R 统一返回结果，包含教师信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        try {
            Long id = null;
            
            // 1. 优先从 Token 获取用户 ID
            String token = request.getHeader("token");
            if (!StringUtils.hasText(token)) {
                token = request.getParameter("token");
            }
            
            if (StringUtils.hasText(token)) {
                TokenEntity tokenEntity = tokenService.getTokenEntity(token);
                if (tokenEntity != null) {
                    id = tokenEntity.getUserid();
                    // 同步到 Session（兼容性）
                    request.getSession().setAttribute("userId", id);
                }
            }
            
            // 2. 如果 Token 无效，尝试从 Session 获取
            if (id == null || id <= 0) {
                id = (Long) request.getSession().getAttribute("userId");
            }
            
            // 3. 验证用户 ID
            if (id == null || id <= 0) {
                log.warn("获取当前教师信息失败：无法获取用户 ID");
                return R.error("请先登录");
            }
            
            // 4. 查询教师信息
            JiaoshiEntity user = jiaoshiService.selectById(id);
            if (user == null) {
                log.warn("获取当前教师信息失败：ID{}不存在", id);
                return R.error("教师信息不存在");
            }
            
            return R.ok().put("data", user);
            
        } catch (Exception e) {
            log.error("获取当前教师信息异常：", e);
            return R.error("获取信息失败");
        }
    }

    /**
     * 检查账号是否可用
     * 功能：注册时检查工号是否已被使用
     */
    @IgnoreAuth
    @RequestMapping("/check-account")
    public R checkAccount(@RequestParam String account) {
        try {
            if (!StringUtils.hasText(account)) {
                return R.error("账号不能为空");
            }
            
            // 账号格式校验：只能是字母、数字、下划线，长度7-20位
            if (!account.matches("^[a-zA-Z0-9_]{7,20}$")) {
                return R.error("账号格式不正确，只能包含字母、数字和下划线，长度7-20位");
            }
            
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", account);
            JiaoshiEntity user = jiaoshiService.selectOne(queryWrapper);
            
            return R.ok().put("available", user == null);
        } catch (Exception e) {
            log.error("检查账号异常：", e);
            return R.error("检查失败，请重试");
        }
    }

    /**
     * 密码重置
     * 功能：将忘记密码重置为默认密码 123456
     * 
     * 注意：此接口需要权限控制，生产环境应限制为管理员使用
     * 
     * @param username 工号
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @IgnoreAuth // 忽略权限验证 (建议添加权限控制)
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        try {
            // 1. 参数校验
            if (!StringUtils.hasText(username)) {
                log.warn("重置密码失败：工号为空");
                return R.error("工号不能为空");
            }
            
            // 2. 查询教师
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", username);
            JiaoshiEntity user = jiaoshiService.selectOne(queryWrapper);
            
            if (user == null) {
                log.warn("重置密码失败：工号{}不存在", username);
                return R.error("账号不存在");
            }
            
            // 3. 重置密码为默认密码 (使用 BCrypt 加密)
            user.setMima(passwordEncoder.encode("123456"));
            jiaoshiService.updateById(user);
            
            log.info("教师 {} 密码重置成功", username);
            return R.ok("密码已重置为：123456");
            
        } catch (Exception e) {
            log.error("重置密码异常：", e);
            return R.error("重置失败，请联系管理员");
        }
    }

    /**
     * 后端分页列表查询
     * 功能：管理员查询所有教师信息
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jiaoshi 教师实体 (用于条件查询)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, 
                  JiaoshiEntity jiaoshi,
                  HttpServletRequest request) {
        try {
            log.info("教师分页查询，参数：{}", params);
            
            // 1. 构建查询条件
            EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<>();
            
            // 2. 处理姓名模糊查询
            if (params.containsKey("jiaoshixingming") && params.get("jiaoshixingming") != null) {
                String xingming = params.get("jiaoshixingming").toString();
                if (!xingming.isEmpty()) {
                    ew.like("jiaoshixingming", xingming.replace("%", ""));
                    log.info("教师姓名模糊查询：{}", xingming);
                }
            }
            
            // 3. 执行分页查询
            PageUtils page = jiaoshiService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiaoshi), params), params)
            );
            
            log.info("教师分页查询成功，总数：{}", page.getTotal());
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("教师分页查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端分页列表查询
     * 功能：对外公开的教师信息查询
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jiaoshi 教师实体 (用于条件查询)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, 
                  JiaoshiEntity jiaoshi,
                  HttpServletRequest request) {
        try {
            // 1. 构建查询条件
            EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<>();
            
            // 2. 执行分页查询
            PageUtils page = jiaoshiService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiaoshi), params), params)
            );
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("教师前端列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 简单列表查询 (不分页)
     * 功能：获取所有符合条件的教师信息
     * 
     * @param jiaoshi 教师实体 (用于条件查询)
     * @return R 统一返回结果，包含列表数据
     */
    @RequestMapping("/lists")
    public R list(JiaoshiEntity jiaoshi) {
        try {
            // 1. 构建查询条件 (精确匹配)
            EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jiaoshi, "jiaoshi"));
            
            // 2. 查询列表
            return R.ok().put("data", jiaoshiService.selectListView(ew));
            
        } catch (Exception e) {
            log.error("教师列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 查询单个教师信息
     * 功能：根据条件查询教师详情
     * 
     * @param jiaoshi 教师实体 (用于条件查询)
     * @return R 统一返回结果，包含教师视图数据
     */
    @RequestMapping("/query")
    public R query(JiaoshiEntity jiaoshi) {
        try {
            // 1. 构建查询条件
            EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jiaoshi, "jiaoshi"));
            
            // 2. 查询视图数据 (关联查询)
            JiaoshiView jiaoshiView = jiaoshiService.selectView(ew);
            
            if (jiaoshiView == null) {
                log.warn("查询教师信息失败：未找到符合条件的记录");
                return R.error("教师信息不存在");
            }
            
            return R.ok("查询教师成功").put("data", jiaoshiView);
            
        } catch (Exception e) {
            log.error("查询教师信息异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 后端详情查询
     * 功能：根据 ID 查询教师详细信息
     * 
     * @param id 教师 ID
     * @return R 统一返回结果，包含教师详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询教师详情失败：ID 非法，ID: {}", id);
                return R.error("教师 ID 非法");
            }
            
            // 2. 查询教师信息
            JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
            
            if (jiaoshi == null) {
                log.warn("查询教师详情失败：ID{}不存在", id);
                return R.error("教师信息不存在");
            }
            
            return R.ok().put("data", jiaoshi);
            
        } catch (Exception e) {
            log.error("查询教师详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端详情查询
     * 功能：根据 ID 查询教师详细信息 (前台展示)
     * 
     * @param id 教师 ID
     * @return R 统一返回结果，包含教师详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询教师详情失败：ID 非法，ID: {}", id);
                return R.error("教师 ID 非法");
            }
            
            // 2. 查询教师信息
            JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
            
            if (jiaoshi == null) {
                log.warn("查询教师详情失败：ID{}不存在", id);
                return R.error("教师信息不存在");
            }
            
            return R.ok().put("data", jiaoshi);
            
        } catch (Exception e) {
            log.error("查询教师详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 保存教师信息 (后台管理)
     * 功能：管理员添加教师
     * 
     * @param jiaoshi 教师实体
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @OperationLog("保存教师信息")
    @RequestMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request) {
        try {
            // 1. 基础参数校验
            if (!StringUtils.hasText(jiaoshi.getGonghao())) {
                log.warn("保存教师失败：工号为空");
                return R.error("工号不能为空");
            }
            
            // 2. 检查工号是否已存在
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", jiaoshi.getGonghao());
            if (jiaoshiService.selectOne(queryWrapper) != null) {
                log.warn("保存教师失败：工号{}已存在", jiaoshi.getGonghao());
                return R.error("用户已存在");
            }
            
            // 3. 密码加密 (如果有密码)
            if (StringUtils.hasText(jiaoshi.getMima())) {
                jiaoshi.setMima(passwordEncoder.encode(jiaoshi.getMima()));
            }
            
            // 4. 生成 ID 并保存
            jiaoshi.setId(IdWorker.getId());
            ValidatorUtils.validateEntity(jiaoshi);
            jiaoshiService.insert(jiaoshi);
            
            log.info("保存教师信息成功，ID: {}, 工号：{}", 
                     jiaoshi.getId(), jiaoshi.getGonghao());
            return R.ok("保存成功");
            
        } catch (Exception e) {
            log.error("保存教师信息异常：", e);
            return R.error("保存失败，请联系管理员");
        }
    }

    /**
     * 保存教师信息 (前端)
     * 功能：在前台添加教师
     * 
     * @param jiaoshi 教师实体
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @OperationLog("添加教师信息")
    @RequestMapping("/add")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R add(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request) {
        try {
            // 1. 基础参数校验
            if (!StringUtils.hasText(jiaoshi.getGonghao())) {
                log.warn("添加教师失败：工号为空");
                return R.error("工号不能为空");
            }
            
            // 2. 检查工号是否已存在
            EntityWrapper<JiaoshiEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("gonghao", jiaoshi.getGonghao());
            if (jiaoshiService.selectOne(queryWrapper) != null) {
                log.warn("添加教师失败：工号{}已存在", jiaoshi.getGonghao());
                return R.error("用户已存在");
            }
            
            // 3. 密码加密 (如果有密码)
            if (StringUtils.hasText(jiaoshi.getMima())) {
                jiaoshi.setMima(passwordEncoder.encode(jiaoshi.getMima()));
            }
            
            // 4. 生成 ID 并保存
            jiaoshi.setId(IdWorker.getId());
            ValidatorUtils.validateEntity(jiaoshi);
            jiaoshiService.insert(jiaoshi);
            
            log.info("添加教师信息成功，ID: {}, 工号：{}", 
                     jiaoshi.getId(), jiaoshi.getGonghao());
            return R.ok("添加成功");
            
        } catch (Exception e) {
            log.error("添加教师信息异常：", e);
            return R.error("添加失败，请联系管理员");
        }
    }

    /**
     * 验证原密码
     * 功能：前端修改密码时验证原密码是否正确
     * 
     * @param request HTTP 请求
     * @param params 包含oldPassword参数
     * @return R 统一返回结果
     */
    @RequestMapping("/verifyPassword")
    public R verifyPassword(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        try {
            // 1. 获取当前登录用户
            String token = request.getHeader("token");
            if (!StringUtils.hasText(token)) {
                token = request.getParameter("token");
            }
            
            TokenEntity tokenEntity = tokenService.getTokenEntity(token);
            if (tokenEntity == null) {
                return R.error("未登录或登录已过期");
            }
            
            // 2. 查询教师信息
            JiaoshiEntity jiaoshi = jiaoshiService.selectById(tokenEntity.getUserid());
            if (jiaoshi == null) {
                return R.error("用户不存在");
            }
            
            // 3. 验证密码
            String oldPassword = params.get("oldPassword") != null ? params.get("oldPassword").toString() : "";
            if (!StringUtils.hasText(oldPassword)) {
                return R.error("原密码不能为空");
            }
            
            boolean matches = passwordEncoder.matches(oldPassword, jiaoshi.getMima());
            if (!matches) {
                return R.error("原密码错误");
            }
            
            return R.ok("密码验证成功");
            
        } catch (Exception e) {
            log.error("验证密码异常：", e);
            return R.error("验证失败");
        }
    }

    /**
     * 修改教师信息
     * 功能：更新教师信息
     * 
     * @param jiaoshi 教师实体 (包含更新后的数据)
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @OperationLog("修改教师信息")
    @RequestMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request) {
        try {
            // 1. 参数校验
            if (jiaoshi.getId() == null || jiaoshi.getId() <= 0) {
                log.warn("修改教师失败：ID 非法，ID: {}", jiaoshi.getId());
                return R.error("教师 ID 非法");
            }
            
            // 2. 如果密码不为空，进行加密处理
            if (StringUtils.hasText(jiaoshi.getMima())) {
                // 判断是否是BCrypt加密过的密码（以$2a$开头）
                if (!jiaoshi.getMima().startsWith("$2a$")) {
                    // 明文密码，需要加密
                    jiaoshi.setMima(passwordEncoder.encode(jiaoshi.getMima()));
                    log.info("检测到明文密码，已进行BCrypt加密");
                }
            }
            
            // 3. 执行更新
            jiaoshiService.updateById(jiaoshi);
            
            // 4. 【数据联动】同步更新所有关联表中的冗余数据
            try {
                dataSyncService.syncJiaoshiInfo(jiaoshi);
                log.info("教师信息联动同步完成 - 工号: {}", jiaoshi.getGonghao());
            } catch (Exception syncError) {
                log.error("教师信息联动同步失败，但主数据已更新 - 工号: {}", jiaoshi.getGonghao(), syncError);
                // 注意：这里不抛出异常，因为主数据已经更新成功
                // 同步失败不影响主流程，可以通过定时任务或手动脚本修复
            }
            
            log.info("修改教师信息成功，ID: {}, 工号：{}", 
                     jiaoshi.getId(), jiaoshi.getGonghao());
            return R.ok("修改成功");
            
        } catch (Exception e) {
            log.error("修改教师信息 ID{}异常：", jiaoshi.getId(), e);
            return R.error("修改失败，请联系管理员");
        }
    }

    /**
     * 删除教师信息
     * 功能：批量删除教师记录
     * 
     * @param ids 教师 ID 数组
     * @return R 统一返回结果
     */
    @OperationLog("删除教师信息")
    @RequestMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除教师失败：ID 数组为空");
            return R.error("请选择要删除的教师");
        }
        
        try {
            // 2. 批量删除
            jiaoshiService.deleteBatchIds(Arrays.asList(ids));
            
            log.info("删除教师信息成功，IDs: {}", Arrays.toString(ids));
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除教师 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
        }
    }

    /**
     * 提醒功能接口
     * 功能：统计符合条件的教师数量
     * 
     * @param columnName 字段名
     * @param request HTTP 请求
     * @param type 类型 ("1": 今天，"2": 区间范围)
     * @param map 查询参数
     * @return R 统一返回结果，包含数量
     */
    @RequestMapping("/remind/{columnName}/{type}")
    public R remindCount(@PathVariable("columnName") String columnName, 
                         HttpServletRequest request,
                         @PathVariable("type") String type, 
                         @RequestParam Map<String, Object> map) {
        try {
            // 1. 设置字段名
            map.put("column", columnName);
            map.put("type", type);
            
            // 2. 处理时间区间 (类型 2：自定义区间)
            if ("2".equals(type)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                
                // 2.1 处理开始时间
                if (map.get("remindstart") != null) {
                    Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
                    c.setTime(new Date());
                    c.add(Calendar.DAY_OF_MONTH, remindStart);
                    map.put("remindstart", sdf.format(c.getTime()));
                }
                
                // 2.2 处理结束时间
                if (map.get("remindend") != null) {
                    Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
                    c.setTime(new Date());
                    c.add(Calendar.DAY_OF_MONTH, remindEnd);
                    map.put("remindend", sdf.format(c.getTime()));
                }
            }
            
            // 3. 构建查询条件
            Wrapper<JiaoshiEntity> wrapper = new EntityWrapper<>();
            if (map.get("remindstart") != null) {
                wrapper.ge(columnName, map.get("remindstart"));
            }
            if (map.get("remindend") != null) {
                wrapper.le(columnName, map.get("remindend"));
            }
            
            // 4. 统计数量
            int count = jiaoshiService.selectCount(wrapper);
            
            log.debug("教师提醒查询成功，字段：{}, 类型：{}, 数量：{}", columnName, type, count);
            return R.ok().put("count", count);
            
        } catch (Exception e) {
            log.error("教师提醒查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }
}
