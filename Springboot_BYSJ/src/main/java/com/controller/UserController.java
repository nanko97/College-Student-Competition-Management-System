package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.entity.UserEntity;
import com.service.TokenService;
import com.service.UserService;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录相关
 * 优化点：密码加密、安全校验、异常处理、代码规范、性能优化
 * 适配：旧版MyBatis-Plus（移除LambdaQueryWrapper/IPage，仅用EntityWrapper/Page）
 */
@RequestMapping("users")
@RestController
@Slf4j // 日志注解，便于问题排查
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    // 密码加密器（Spring Security标准加密方式）
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 登录失败次数限制（配置化）
    @Value("${login.max-fail-count:5}")
    private int maxFailCount;

    // 存储登录失败次数（线程安全，生产环境可替换为Redis）
    private final Map<String, Integer> loginFailCountMap = new ConcurrentHashMap<>();

    // 验证码session key常量
    private static final String CAPTCHA_SESSION_KEY = "SESSION_CAPTCHA";
    // 默认重置密码
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 登录
     * 优化：密码加密校验、验证码校验、登录失败次数限制、日志输出
     */
    @IgnoreAuth
    @PostMapping(value = "/login")
    public R login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String captcha,
            HttpServletRequest request) {
        // 1. 基础参数校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("登录失败：用户名或密码为空，用户名：{}", username);
            return R.error("用户名和密码不能为空");
        }

        // 2. 验证码校验（生产环境建议开启）
        HttpSession session = request.getSession();
        String sessionCaptcha = (String) session.getAttribute(CAPTCHA_SESSION_KEY);
        if (!StringUtils.hasText(captcha) || !captcha.equalsIgnoreCase(sessionCaptcha)) {
            log.warn("登录失败：验证码错误，用户名：{}，输入验证码：{}", username, captcha);
            return R.error("验证码错误");
        }
        // 验证成功后清除验证码，防止重复使用
        session.removeAttribute(CAPTCHA_SESSION_KEY);

        // 3. 检查登录失败次数
        Integer failCount = loginFailCountMap.getOrDefault(username, 0);
        if (failCount >= maxFailCount) {
            log.warn("登录失败：用户名{}登录失败次数超过限制（{}次）", username, maxFailCount);
            return R.error("登录失败次数过多，请10分钟后再试");
        }

        try {
            // 4. 查询用户（适配旧版MyBatis-Plus：EntityWrapper）
            EntityWrapper<UserEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("username", username);
            UserEntity user = userService.selectOne(queryWrapper); // 旧版方法：selectOne

            // 5. 用户不存在或密码错误
            if (user == null) {
                loginFailCountMap.put(username, failCount + 1);
                log.warn("登录失败：用户名{}不存在", username);
                return R.error("账号或密码不正确");
            }

            // 6. 密码校验（BCrypt加密对比，替换明文equals）
            if (!passwordEncoder.matches(password, user.getPassword())) {
                loginFailCountMap.put(username, failCount + 1);
                log.warn("登录失败：用户名{}密码错误，失败次数：{}", username, failCount + 1);
                return R.error("账号或密码不正确");
            }

            // 7. 登录成功，重置失败次数
            loginFailCountMap.remove(username);
            String token = tokenService.generateToken(user.getId(), username, "users", user.getRole());
            log.info("用户{}登录成功，生成token：{}", username, token.substring(0, 10) + "****");
            return R.ok().put("token", token);

        } catch (Exception e) {
            log.error("用户{}登录异常：", username, e);
            return R.error("登录异常，请联系管理员");
        }
    }

    /**
     * 注册
     * 优化：密码加密、参数校验、日志输出
     */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody UserEntity user) {
        // 1. 基础参数校验
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            log.warn("注册失败：用户名或密码为空，用户名：{}", user.getUsername());
            return R.error("用户名和密码不能为空");
        }

        try {
            // 2. 检查用户是否已存在（适配旧版：EntityWrapper）
            EntityWrapper<UserEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            if (userService.selectOne(queryWrapper) != null) { // 旧版方法：selectOne
                log.warn("注册失败：用户名{}已存在", user.getUsername());
                return R.error("用户已存在");
            }

            // 3. 密码加密（注册时加密存储，避免明文）
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // 补充默认值（如创建时间，需根据实体类调整）
            if (user.getAddtime() == null) {
                user.setAddtime(new Date());
            }

            // 4. 保存用户（旧版方法：insert）
            userService.insert(user);
            log.info("用户{}注册成功", user.getUsername());
            return R.ok("注册成功");

        } catch (Exception e) {
            log.error("用户{}注册异常：", user.getUsername(), e);
            return R.error("注册失败，请联系管理员");
        }
    }

    /**
     * 退出
     * 优化：token失效（需配合TokenService实现）、日志输出
     */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        try {
            // 1. 清除session
            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");
            session.invalidate();

            // 2. Token失效（需在TokenService中实现token黑名单）
            String token = request.getHeader("token");
            if (StringUtils.hasText(token)) {
                tokenService.invalidateToken(token);
            }

            log.info("用户ID{}退出成功", userId);
            return R.ok("退出成功");
        } catch (Exception e) {
            log.error("退出登录异常：", e);
            return R.error("退出失败，请重试");
        }
    }

    /**
     * 密码重置
     * 优化：密码加密、参数校验、日志输出
     */
    @IgnoreAuth
    @PostMapping(value = "/resetPass") // 改为Post，更符合REST规范
    public R resetPass(@RequestParam String username, HttpServletRequest request) {
        // 1. 参数校验
        if (!StringUtils.hasText(username)) {
            log.warn("重置密码失败：用户名为空");
            return R.error("账号不能为空");
        }

        try {
            // 2. 查询用户（适配旧版：EntityWrapper）
            EntityWrapper<UserEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("username", username);
            UserEntity user = userService.selectOne(queryWrapper); // 旧版方法：selectOne
            if (user == null) {
                log.warn("重置密码失败：用户名{}不存在", username);
                return R.error("账号不存在");
            }

            // 3. 重置密码（加密存储，避免明文）
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            userService.updateById(user); // 旧版方法：updateById
            log.info("用户{}密码重置成功，默认密码：{}", username, DEFAULT_PASSWORD);
            return R.ok("密码已重置为：" + DEFAULT_PASSWORD);

        } catch (Exception e) {
            log.error("用户{}重置密码异常：", username, e);
            return R.error("密码重置失败，请联系管理员");
        }
    }

    /**
     * 分页列表
     * 优化：适配旧版MyBatis-Plus、分页逻辑、日志输出
     */
    @PostMapping("/page") // 改为Post，避免Get参数过长
    public R page(@RequestParam Map<String, Object> params, UserEntity user) {
        try {
            EntityWrapper<UserEntity> ew = new EntityWrapper<>();
            // 1. 构建查询条件（复用你的MPUtil原有方法）
            MPUtil.allLike(ew, user);   // 模糊查询
            MPUtil.between(ew, params); // 时间区间查询
            MPUtil.sort(ew, params);    // 排序

            // 2. 核心修改：用Query工具类创建分页对象（适配你的项目）
            Page<UserEntity> page = new Query<UserEntity>(params).getPage();
            // 3. 执行分页查询（旧版selectPage方法）
            page = userService.selectPage(page, ew);
            // 4. 封装分页结果
            PageUtils pageUtils = new PageUtils(page);

            return R.ok().put("data", pageUtils);
        } catch (Exception e) {
            log.error("用户分页查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 列表查询
     * 优化：适配旧版MyBatis-Plus、参数校验
     */
    @PostMapping("/list") // 改为Post，更规范
    public R list(@RequestBody(required = false) UserEntity user) {
        try {
            EntityWrapper<UserEntity> ew = new EntityWrapper<>();
            if (user != null) {
                ew.allEq(MPUtil.allEQMapPre(user, "user"));
            }
            // 适配旧版：selectListView（保留原有业务方法）
            return R.ok().put("data", userService.selectListView(ew));
        } catch (Exception e) {
            log.error("用户列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 用户信息
     * 优化：参数校验、异常处理
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) { // 改为Long类型，匹配实体类主键类型
        // 1. 参数校验
        if (id == null || id <= 0) {
            log.warn("查询用户信息失败：ID非法，ID：{}", id);
            return R.error("用户ID非法");
        }

        try {
            // 旧版方法：selectById
            UserEntity user = userService.selectById(id);
            if (user == null) {
                log.warn("查询用户信息失败：ID{}不存在", id);
                return R.error("用户不存在");
            }
            // 隐藏密码，避免敏感信息泄露
            user.setPassword(null);
            return R.ok().put("data", user);
        } catch (Exception e) {
            log.error("查询用户ID{}信息异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 获取当前登录用户信息
     * 优化：参数校验、敏感信息隐藏
     */
    @GetMapping("/session")
    public R getCurrUser(HttpServletRequest request) {
        try {
            Long id = (Long) request.getSession().getAttribute("userId");
            if (id == null || id <= 0) {
                log.warn("获取当前用户信息失败：session中无用户ID");
                return R.error("请先登录");
            }

            // 旧版方法：selectById
            UserEntity user = userService.selectById(id);
            if (user == null) {
                log.warn("获取当前用户信息失败：ID{}不存在", id);
                return R.error("用户信息不存在");
            }
            // 隐藏密码
            user.setPassword(null);
            return R.ok().put("data", user);
        } catch (Exception e) {
            log.error("获取当前用户信息异常：", e);
            return R.error("获取用户信息失败");
        }
    }

    /**
     * 保存用户
     * 优化：密码加密、参数校验、重复用户校验
     */
    @PostMapping("/save")
    public R save(@RequestBody UserEntity user) {
        // 1. 参数校验
        if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            log.warn("保存用户失败：用户名或密码为空，用户名：{}", user.getUsername());
            return R.error("用户名和密码不能为空");
        }

        try {
            // 2. 检查用户是否已存在（适配旧版：EntityWrapper）
            EntityWrapper<UserEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            if (userService.selectOne(queryWrapper) != null) { // 旧版方法：selectOne
                log.warn("保存用户失败：用户名{}已存在", user.getUsername());
                return R.error("用户已存在");
            }

            // 3. 密码加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getAddtime() == null) {
                user.setAddtime(new Date());
            }

            // 旧版方法：insert
            userService.insert(user);
            log.info("保存用户{}成功", user.getUsername());
            return R.ok("保存成功");
        } catch (Exception e) {
            log.error("保存用户{}异常：", user.getUsername(), e);
            return R.error("保存失败，请联系管理员");
        }
    }

    /**
     * 修改用户
     * 优化：适配旧版MyBatis-Plus、密码加密、重复校验逻辑
     */
    @PostMapping("/update")
    public R update(@RequestBody UserEntity user) {
        // 1. 参数校验
        if (user.getId() == null || user.getId() <= 0) {
            log.warn("修改用户失败：ID非法，ID：{}", user.getId());
            return R.error("用户ID非法");
        }

        try {
            // 2. 用户名重复校验（排除自身，适配旧版）
            EntityWrapper<UserEntity> queryWrapper = new EntityWrapper<>();
            queryWrapper.eq("username", user.getUsername())
                    .ne("id", user.getId()); // 旧版：字符串字段名
            if (userService.selectOne(queryWrapper) != null) {
                log.warn("修改用户失败：用户名{}已存在，当前ID：{}", user.getUsername(), user.getId());
                return R.error("用户名已存在。");
            }

            // 3. 若修改密码，重新加密
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 不修改密码时，保留原密码
                UserEntity oldUser = userService.selectById(user.getId());
                user.setPassword(oldUser.getPassword());
            }

            // 旧版方法：updateById
            userService.updateById(user);
            log.info("修改用户ID{}成功", user.getId());
            return R.ok("修改成功");
        } catch (Exception e) {
            log.error("修改用户ID{}异常：", user.getId(), e);
            return R.error("修改失败，请联系管理员");
        }
    }

    /**
     * 删除用户
     * 优化：适配旧版MyBatis-Plus、参数校验、日志输出
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除用户失败：ID数组为空");
            return R.error("请选择要删除的用户");
        }

        try {
            // 旧版方法：deleteBatchIds
            userService.deleteBatchIds(Arrays.asList(ids));
            log.info("删除用户成功，ID列表：{}", Arrays.toString(ids));
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("删除用户ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
        }
    }
}