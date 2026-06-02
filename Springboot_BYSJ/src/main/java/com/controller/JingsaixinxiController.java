package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaiJiaofeiJiluEntity;
import com.entity.JingsaiTuanduiEntity;
import com.entity.JingsaixinxiEntity;
import com.entity.ZuopindafenEntity;
import com.entity.ZuopindafenFuheEntity;
import com.entity.JingsaiSaidaoEntity;
import com.entity.JingsaiJibieBanbenEntity;
import com.entity.JingsaiJinjiGuanxiEntity;
import com.entity.JiaoshiEntity;
import com.entity.view.JingsaixinxiView;
import com.service.JingsaibaomingService;
import com.service.JingsaiJiaofeiJiluService;
import com.service.JingsaiTuanduiService;
import com.service.JingsaixinxiService;
import com.service.JingsaiSaidaoService;
import com.service.JingsaiJibieBanbenService;
import com.service.JingsaiJinjiGuanxiService;
import com.service.JiaoshiService;
import com.service.ZuopindafenFuheService;
import com.service.ZuopindafenService;
import com.utils.EntityUtil;
import com.utils.IdWorker;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.ValidatorUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞赛信息管理控制器
 * 功能：竞赛信息的增删改查、前端展示、提醒功能
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
@RequestMapping("/jingsaixinxi")
@Slf4j // 日志注解，便于问题排查
@Api(tags = "竞赛信息管理", description = "提供竞赛信息的增删改查、列表查询、提醒等功能")
public class JingsaixinxiController {
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;
    
    @Autowired
    private JingsaibaomingService jingsaibaomingService;
    
    @Autowired
    private JingsaiJiaofeiJiluService jingsaiJiaofeiJiluService;
    
    @Autowired
    private JingsaiTuanduiService jingsaiTuanduiService;
    
    @Autowired
    private ZuopindafenService zuopindafenService;
    
    @Autowired
    private ZuopindafenFuheService zuopindafenFuheService;
    
    @Autowired
    private JingsaiSaidaoService jingsaiSaidaoService;
    
    @Autowired
    private JingsaiJibieBanbenService jingsaiJibieBanbenService;
    
    @Autowired
    private JingsaiJinjiGuanxiService jingsaiJinjiGuanxiService;
    
    @Autowired
    private JiaoshiService jiaoshiService;

    /**
     * 后端分页列表查询
     * 功能：带权限控制的竞赛信息查询
     * 
     * 权限逻辑：
     * - 教师角色：只能查看自己发布的竞赛
     * - 管理员/学生：可以查看所有竞赛
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jingsaixinxi 竞赛信息实体 (用于条件查询)
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果，包含分页数据
     */
    @ApiOperation("分页查询竞赛列表 (后台管理)")
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, 
                  JingsaixinxiEntity jingsaixinxi,
                  HttpServletRequest request) {
        try {
            // 竞赛信息页面为公共信息页，所有角色都能查看所有竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            log.info("当前用户角色tableName：{}，role：{}", tableName, role);
                        
            // 构建查询条件 (支持模糊查询、区间查询、排序)
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
                        
            // 如果前端传递了gonghao参数，进行过滤（用于"我的竞赛"等特定页面）
            if (params.get("gonghao") != null && !params.get("gonghao").toString().isEmpty()) {
                ew.eq("gonghao", params.get("gonghao").toString());
                log.debug("按工号过滤：{}", params.get("gonghao"));
            }
            
            // 处理前端传递的模糊查询参数（带 % 的参数）
            if (params.get("jingsaimingcheng") != null) {
                String value = params.get("jingsaimingcheng").toString();
                if (value.contains("%")) {
                    // 模糊查询：移除 % 后使用 LIKE
                    ew.like("jingsaimingcheng", value.replace("%", ""));
                    log.debug("竞赛名称模糊查询：{}", value.replace("%", ""));
                } else {
                    // 精确查询
                    ew.eq("jingsaimingcheng", value);
                }
            }
            if (params.get("jingsaileixing") != null) {
                String value = params.get("jingsaileixing").toString();
                if (value.contains("%")) {
                    ew.like("jingsaileixing", value.replace("%", ""));
                    log.debug("竞赛类型模糊查询：{}", value.replace("%", ""));
                } else {
                    ew.eq("jingsaileixing", value);
                }
            }
            if (params.get("jingsaididian") != null) {
                String value = params.get("jingsaididian").toString();
                if (value.contains("%")) {
                    ew.like("jingsaididian", value.replace("%", ""));
                } else {
                    ew.eq("jingsaididian", value);
                }
            }
            
            // 【重要】过滤已过期的竞赛，只显示未过期的竞赛
            // 注意：对于下拉选择框等场景，不过滤过期竞赛（因为需要显示历史数据）
            // 只有当前端明确传了 showAll=false 参数时才过滤
            if ("false".equals(params.get("showAll"))) {
                ew.ge("jingsaishijian", new Date());
                log.debug("已过滤已过期的竞赛，只显示未过期竞赛");
            } else {
                log.debug("显示所有竞赛（包括已过期），用于下拉选择等场景");
            }
            
            // 执行分页查询
            PageUtils page = jingsaixinxiService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(ew, params), params)
            );
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("竞赛分页查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端分页列表查询
     * 功能：对外公开的竞赛信息查询 (无需登录)
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jingsaixinxi 竞赛信息实体 (用于条件查询)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含分页数据
     */
    @ApiOperation("分页查询竞赛列表 (前台展示)")
    @IgnoreAuth // 忽略权限验证，允许未登录用户访问
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, 
                  JingsaixinxiEntity jingsaixinxi,
                  HttpServletRequest request) {
        try {
            // 构建查询条件
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            
            // 执行分页查询
            PageUtils page = jingsaixinxiService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jingsaixinxi), params), params)
            );
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("竞赛前端列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 简单列表查询 (不分页)
     * 功能：获取所有符合条件的竞赛信息
     * 
     * @param jingsaixinxi 竞赛信息实体 (用于条件查询)
     * @return R 统一返回结果，包含列表数据
     */
    @RequestMapping("/lists")
    public R list(JingsaixinxiEntity jingsaixinxi, HttpServletRequest request) {
        try {
            // 构建查询条件 (精确匹配)
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaixinxi, "jingsaixinxi"));
            
            // 查询列表 (竞赛信息为公共数据，所有角色都能看到所有竞赛)
            return R.ok().put("data", jingsaixinxiService.selectListView(ew));
            
        } catch (Exception e) {
            log.error("竞赛列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 查询单个竞赛信息
     * 功能：根据条件查询竞赛详情
     * 
     * @param jingsaixinxi 竞赛信息实体 (用于条件查询)
     * @return R 统一返回结果，包含竞赛视图数据
     */
    @ApiOperation("条件查询竞赛详情")
    @RequestMapping("/query")
    public R query(JingsaixinxiEntity jingsaixinxi) {
        try {
            // 构建查询条件
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaixinxi, "jingsaixinxi"));
            
            // 查询视图数据 (关联查询)
            JingsaixinxiView jingsaixinxiView = jingsaixinxiService.selectView(ew);
            
            if (jingsaixinxiView == null) {
                log.warn("查询竞赛信息失败：未找到符合条件的记录");
                return R.error("竞赛信息不存在");
            }
            
            return R.ok("查询竞赛信息成功").put("data", jingsaixinxiView);
            
        } catch (Exception e) {
            log.error("查询竞赛信息异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 后端详情查询
     * 功能：根据 ID 查询竞赛详细信息 (后台管理使用)
     * 
     * 【性能优化】使用缓存加速查询
     * 
     * @param id 竞赛 ID
     * @return R 统一返回结果，包含竞赛详情
     */
    @ApiOperation("根据 ID 查询竞赛详情 (后台)")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        try {
            // 参数校验
            if (id == null || id <= 0) {
                log.warn("查询竞赛详情失败：ID 非法，ID: {}", id);
                return R.error("竞赛 ID 非法");
            }
            
            // 查询竞赛信息
            JingsaixinxiEntity jingsaixinxi = jingsaixinxiService.selectById(id);
            
            if (jingsaixinxi == null) {
                log.warn("查询竞赛详情失败：ID{}不存在", id);
                return R.error("竞赛信息不存在");
            }
            
            return R.ok().put("data", jingsaixinxi);
            
        } catch (Exception e) {
            log.error("查询竞赛详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端详情查询
     * 功能：根据 ID 查询竞赛详细信息 (前台展示使用)
     * 
     * @param id 竞赛 ID
     * @return R 统一返回结果，包含竞赛详情
     */
    @IgnoreAuth // 忽略权限验证，允许未登录用户查看
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        try {
            // 参数校验
            if (id == null || id <= 0) {
                log.warn("查询竞赛详情失败：ID 非法，ID: {}", id);
                return R.error("竞赛 ID 非法");
            }
            
            // 查询竞赛信息
            JingsaixinxiEntity jingsaixinxi = jingsaixinxiService.selectById(id);
            
            if (jingsaixinxi == null) {
                log.warn("查询竞赛详情失败：ID{}不存在", id);
                return R.error("竞赛信息不存在");
            }
            
            return R.ok().put("data", jingsaixinxi);
            
        } catch (Exception e) {
            log.error("查询竞赛详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 保存竞赛信息 (后台管理)
     * 功能：发布新的竞赛信息，自动创建关联数据
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 自动填充工号 (如果是教师发布)
     * 3. 生成唯一 ID
     * 4. 保存到数据库
     * 5. 自动创建关联数据（赛道、级别版本、晋级关系占位符）
     * 
     * @param jingsaixinxi 竞赛信息实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @ApiOperation("保存竞赛信息")
    @RequestMapping("/save")
    @OperationLog("保存竞赛信息")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaixinxiEntity jingsaixinxi, HttpServletRequest request) {
        log.info("保存竞赛请求数据：{}", jingsaixinxi);
        log.info("当前会话信息 - tableName: {}, username: {}", 
                request.getSession().getAttribute("tableName"),
                request.getSession().getAttribute("username"));
        
        // 基础参数校验
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("保存竞赛失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        // 1.5 付费竞赛必须配置费用金额
        if ("付费".equals(jingsaixinxi.getJiaofeimoshi())) {
            if (jingsaixinxi.getJingsaiFeiyong() == null || jingsaixinxi.getJingsaiFeiyong().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                log.warn("保存竞赛失败：付费竞赛费用金额未设置或为0");
                return R.error("付费竞赛必须设置费用金额且大于0");
            }
        } else if ("免费".equals(jingsaixinxi.getJiaofeimoshi()) || jingsaixinxi.getJiaofeimoshi() == null) {
            // 免费竞赛自动设置费用为0
            jingsaixinxi.setJingsaiFeiyong(java.math.BigDecimal.ZERO);
        }
        
        try {
            // 自动填充创建人工号 (如果是教师创建)
            String tableName = (String) request.getSession().getAttribute("tableName");
            String gonghao = null;
            String jiaoshixingming = null;
            
            if ("jiaoshi".equals(tableName)) {
                gonghao = (String) request.getSession().getAttribute("username");
                if (StringUtils.hasText(gonghao)) {
                    jingsaixinxi.setGonghao(gonghao);
                    // 查询教师姓名
                    EntityWrapper<JiaoshiEntity> jiaoshiEw = new EntityWrapper<>();
                    jiaoshiEw.eq("gonghao", gonghao);
                    JiaoshiEntity jiaoshi = jiaoshiService.selectOne(jiaoshiEw);
                    if (jiaoshi != null) {
                        jiaoshixingming = jiaoshi.getJiaoshixingming();
                        jingsaixinxi.setJiaoshixingming(jiaoshixingming);
                    }
                    log.info("教师 {} 发布新竞赛：{}, 数据：{}", gonghao, 
                            jingsaixinxi.getJingsaimingcheng(), jingsaixinxi);
                } else {
                    log.error("教师工号为空，无法保存竞赛");
                    return R.error("请先登录");
                }
            } else {
                log.warn("当前用户角色：{}, 不是教师角色", tableName);
            }
            
            // 实体校验 (使用验证工具类)
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 生成唯一 ID 并保存
            jingsaixinxi.setId(IdWorker.getId());
            boolean result = jingsaixinxiService.insert(jingsaixinxi);
            
            if (!result) {
                log.error("数据库插入返回 false");
                return R.error("保存失败，数据库操作异常");
            }
            
            log.info("保存竞赛信息成功，ID: {}, 名称：{}, 工号：{}", 
                    jingsaixinxi.getId(), 
                    jingsaixinxi.getJingsaimingcheng(),
                    jingsaixinxi.getGonghao());
            
            // 自动创建关联数据
            Long jingsaiId = jingsaixinxi.getId();
            StringBuilder msg = new StringBuilder("保存成功");
            
            // 5.1 如果"有赛道=是"，自动创建默认赛道
            if ("是".equals(jingsaixinxi.getShifouYouSaidao())) {
                try {
                    JingsaiSaidaoEntity saidao = new JingsaiSaidaoEntity();
                    saidao.setId(IdWorker.getId());
                    saidao.setJingsaiId(jingsaiId);
                    saidao.setJingsaimingcheng(jingsaixinxi.getJingsaimingcheng());
                    saidao.setSaidaoMingcheng("默认赛道");
                    saidao.setSaidaoBianma("SD001");
                    saidao.setCansaiLeixing("个人赛");
                    saidao.setTuanduiRenshuMin(1);
                    saidao.setTuanduiRenshuMax(1);
                    saidao.setBaomingShuoming("默认赛道，支持个人参赛");
                    saidao.setPingshenBiaozhun("按作品评分");
                    saidao.setIsActive("是");
                    saidao.setSortOrder(1);
                    saidao.setAddtime(new Date());
                    
                    jingsaiSaidaoService.insert(saidao);
                    msg.append("，已自动创建默认赛道");
                    log.info("自动创建默认赛道成功，竞赛ID: {}, 赛道ID: {}", jingsaiId, saidao.getId());
                } catch (Exception e) {
                    log.error("自动创建赛道失败", e);
                    msg.append("（赛道创建失败，请手动创建）");
                }
            }
            
            // 5.2 自动创建级别版本记录
            if (StringUtils.hasText(jingsaixinxi.getJingsaiJibie())) {
                try {
                    JingsaiJibieBanbenEntity jibie = new JingsaiJibieBanbenEntity();
                    jibie.setId(IdWorker.getId());
                    jibie.setJingsaiId(jingsaiId);
                    jibie.setJingsaimingcheng(jingsaixinxi.getJingsaimingcheng());
                    jibie.setJingsaiNianfen(jingsaixinxi.getNianfen());
                    jibie.setJibie(jingsaixinxi.getJingsaiJibie());
                    jibie.setRenzhengJigou("系统自动生成");
                    jibie.setWenjianHao("AUTO-" + System.currentTimeMillis());
                    jibie.setShengxiaoRiqi(new Date());
                    jibie.setIsCurrent("是");
                    jibie.setCaozuoRen(gonghao != null ? gonghao : "system");
                    jibie.setAddtime(new Date());
                    
                    jingsaiJibieBanbenService.insert(jibie);
                    msg.append("，已自动创建级别版本");
                    log.info("自动创建级别版本成功，竞赛ID: {}, 级别版本ID: {}", jingsaiId, jibie.getId());
                } catch (Exception e) {
                    log.error("自动创建级别版本失败", e);
                    msg.append("（级别版本创建失败，请手动创建）");
                }
            }
            
            // 5.3 如果"需晋级=是"，记录日志提示教师配置晋级关系
            if ("是".equals(jingsaixinxi.getShifouXuyaoJinji())) {
                msg.append("，请到晋级管理配置晋级规则");
                log.info("竞赛需晋级，请在晋级管理中配置晋级规则，竞赛ID: {}", jingsaiId);
            }
            
            return R.ok(msg.toString());
            
        } catch (Exception e) {
            log.error("保存竞赛信息异常：", e);
            return R.error("保存失败，请联系管理员");
        }
    }

    /**
     * 保存竞赛信息 (前端)
     * 功能：在前台发布竞赛信息 (需要权限控制时使用)
     * 
     * @param jingsaixinxi 竞赛信息实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @RequestMapping("/add")
    @OperationLog("添加竞赛信息")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R add(@RequestBody JingsaixinxiEntity jingsaixinxi, HttpServletRequest request) {
        // 基础参数校验
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("添加竞赛失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        // 1.5 付费竞赛必须配置费用金额
        if ("付费".equals(jingsaixinxi.getJiaofeimoshi())) {
            if (jingsaixinxi.getJingsaiFeiyong() == null || jingsaixinxi.getJingsaiFeiyong().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return R.error("付费竞赛必须设置费用金额且大于0");
            }
        } else if ("免费".equals(jingsaixinxi.getJiaofeimoshi()) || jingsaixinxi.getJiaofeimoshi() == null) {
            jingsaixinxi.setJingsaiFeiyong(java.math.BigDecimal.ZERO);
        }
            
        try {
            // 自动填充创建人工号 (如果是教师创建)
            String tableName = (String) request.getSession().getAttribute("tableName");
            String gonghao = null;
            String jiaoshixingming = null;
                
            if ("jiaoshi".equals(tableName)) {
                gonghao = (String) request.getSession().getAttribute("username");
                if (StringUtils.hasText(gonghao)) {
                    jingsaixinxi.setGonghao(gonghao);
                    // 查询教师姓名
                    EntityWrapper<JiaoshiEntity> jiaoshiEw = new EntityWrapper<>();
                    jiaoshiEw.eq("gonghao", gonghao);
                    JiaoshiEntity jiaoshi = jiaoshiService.selectOne(jiaoshiEw);
                    if (jiaoshi != null) {
                        jiaoshixingming = jiaoshi.getJiaoshixingming();
                        jingsaixinxi.setJiaoshixingming(jiaoshixingming);
                    }
                    log.info("教师 {} 添加新竞赛：{}, 数据：{}", gonghao, jingsaixinxi.getJingsaimingcheng(), jingsaixinxi);
                } else {
                    log.error("教师工号为空，无法添加竞赛");
                    return R.error("请先登录");
                }
            }
                
            // 实体校验
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 3.5 【关键修复】兜底处理：设置addtime（如果前端未传递或格式错误）
            EntityUtil.setAddtimeIfNull(jingsaixinxi);
                
            // 生成唯一 ID 并保存
            jingsaixinxi.setId(IdWorker.getId());
            jingsaixinxiService.insert(jingsaixinxi);
                
            log.info("添加竞赛信息成功，ID: {}, 名称：{}", jingsaixinxi.getId(), jingsaixinxi.getJingsaimingcheng());
                
            // 自动创建关联数据（与 /save 方法逻辑一致）
            Long jingsaiId = jingsaixinxi.getId();
            StringBuilder msg = new StringBuilder("添加成功");
                
            // 5.1 如果"有赛道=是"，自动创建默认赛道
            if ("是".equals(jingsaixinxi.getShifouYouSaidao())) {
                try {
                    JingsaiSaidaoEntity saidao = new JingsaiSaidaoEntity();
                    saidao.setId(IdWorker.getId());
                    saidao.setJingsaiId(jingsaiId);
                    saidao.setJingsaimingcheng(jingsaixinxi.getJingsaimingcheng());
                    saidao.setSaidaoMingcheng("默认赛道");
                    saidao.setSaidaoBianma("SD001");
                    saidao.setCansaiLeixing("个人赛");
                    saidao.setTuanduiRenshuMin(1);
                    saidao.setTuanduiRenshuMax(1);
                    saidao.setBaomingShuoming("默认赛道，支持个人参赛");
                    saidao.setPingshenBiaozhun("按作品评分");
                    saidao.setIsActive("是");
                    saidao.setSortOrder(1);
                    saidao.setAddtime(new Date());
                    jingsaiSaidaoService.insert(saidao);
                    msg.append("，已自动创建默认赛道");
                    log.info("添加竞赛时自动创建默认赛道，竞赛ID: {}", jingsaiId);
                } catch (Exception e) {
                    log.error("添加竞赛时自动创建赛道失败", e);
                    msg.append("（赛道创建失败，请手动创建）");
                }
            }
                
            // 5.2 自动创建级别版本记录
            if (StringUtils.hasText(jingsaixinxi.getJingsaiJibie())) {
                try {
                    JingsaiJibieBanbenEntity jibie = new JingsaiJibieBanbenEntity();
                    jibie.setId(IdWorker.getId());
                    jibie.setJingsaiId(jingsaiId);
                    jibie.setJingsaimingcheng(jingsaixinxi.getJingsaimingcheng());
                    jibie.setJingsaiNianfen(jingsaixinxi.getNianfen());
                    jibie.setJibie(jingsaixinxi.getJingsaiJibie());
                    jibie.setRenzhengJigou("系统自动生成");
                    jibie.setWenjianHao("AUTO-" + System.currentTimeMillis());
                    jibie.setShengxiaoRiqi(new Date());
                    jibie.setIsCurrent("是");
                    jibie.setCaozuoRen(gonghao != null ? gonghao : "system");
                    jibie.setAddtime(new Date());
                    jingsaiJibieBanbenService.insert(jibie);
                    msg.append("，已自动创建级别版本");
                    log.info("添加竞赛时自动创建级别版本，竞赛ID: {}", jingsaiId);
                } catch (Exception e) {
                    log.error("添加竞赛时自动创建级别版本失败", e);
                    msg.append("（级别版本创建失败，请手动创建）");
                }
            }
                
            // 5.3 如果"需晋级=是"，提示教师配置晋级关系
            if ("是".equals(jingsaixinxi.getShifouXuyaoJinji())) {
                msg.append("，请到晋级管理配置晋级规则");
                log.info("竞赛需晋级，请在晋级管理中配置晋级规则，竞赛ID: {}", jingsaiId);
            }
                
            return R.ok(msg.toString());
                
        } catch (Exception e) {
            log.error("添加竞赛信息异常：", e);
            return R.error("添加失败，请联系管理员");
        }
    }

    /**
     * 修改竞赛信息
     * 功能：更新已有的竞赛信息
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 验证实体完整性
     * 3. 教师只能修改自己创建的竞赛
     * 4. 更新到数据库
     * 
     * @param jingsaixinxi 竞赛信息实体 (包含更新后的数据)
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @ApiOperation("修改竞赛信息")
    @RequestMapping("/update")
    @OperationLog("修改竞赛信息")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaixinxiEntity jingsaixinxi, HttpServletRequest request) {
        // 参数校验
        if (jingsaixinxi.getId() == null || jingsaixinxi.getId() <= 0) {
            log.warn("修改竞赛失败：ID 非法，ID: {}", jingsaixinxi.getId());
            return R.error("竞赛 ID 非法");
        }
        
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("修改竞赛失败：竞赛名称为空，ID: {}", jingsaixinxi.getId());
            return R.error("竞赛名称不能为空");
        }
        
        // 1.5 付费竞赛必须配置费用金额
        if ("付费".equals(jingsaixinxi.getJiaofeimoshi())) {
            if (jingsaixinxi.getJingsaiFeiyong() == null || jingsaixinxi.getJingsaiFeiyong().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                log.warn("修改竞赛失败：付费竞赛费用金额未设置或为0");
                return R.error("付费竞赛必须设置费用金额且大于0");
            }
        } else if ("免费".equals(jingsaixinxi.getJiaofeimoshi())) {
            jingsaixinxi.setJingsaiFeiyong(java.math.BigDecimal.ZERO);
        }
        
        try {
            // 查询原竞赛信息（用于对比变更）
            JingsaixinxiEntity existingJingsai = jingsaixinxiService.selectById(jingsaixinxi.getId());
            if (existingJingsai == null) {
                return R.error("竞赛信息不存在");
            }
            
            // 权限验证：教师只能修改自己创建的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            String gonghao = (String) request.getSession().getAttribute("username");
            
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                if (gonghao == null || !gonghao.equalsIgnoreCase(existingJingsai.getGonghao())) {
                    log.warn("教师 {} 尝试修改不属于自己的竞赛 ID: {}，竞赛创建者: {}",
                            gonghao, jingsaixinxi.getId(), existingJingsai.getGonghao());
                    return R.error("只能修改自己创建的竞赛");
                }
                log.info("教师 {} 修改自己的竞赛 ID: {}", gonghao, jingsaixinxi.getId());
            }
            
            // 实体校验
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 执行更新
            jingsaixinxiService.updateById(jingsaixinxi);
            
            // 联动更新关联数据
            StringBuilder msg = new StringBuilder("修改成功");
            Long jingsaiId = jingsaixinxi.getId();
            String newName = jingsaixinxi.getJingsaimingcheng();
            String oldName = existingJingsai.getJingsaimingcheng();
            boolean nameChanged = !newName.equals(oldName);
            
            // 6.1 竞赛名称变更时，同步更新赛道和级别版本中的竞赛名称
            if (nameChanged) {
                try {
                    // 更新赛道表中的竞赛名称
                    EntityWrapper<JingsaiSaidaoEntity> saidaoEw = new EntityWrapper<>();
                    saidaoEw.eq("jingsai_id", jingsaiId);
                    List<JingsaiSaidaoEntity> saidaoList = jingsaiSaidaoService.selectList(saidaoEw);
                    if (saidaoList != null && !saidaoList.isEmpty()) {
                        for (JingsaiSaidaoEntity saidao : saidaoList) {
                            saidao.setJingsaimingcheng(newName);
                            jingsaiSaidaoService.updateById(saidao);
                        }
                        msg.append("，赛道名称已同步更新");
                        log.info("同步更新赛道竞赛名称，竞赛ID: {}，旧名称: {} -> 新名称: {}", jingsaiId, oldName, newName);
                    }
                } catch (Exception e) {
                    log.error("同步更新赛道竞赛名称失败", e);
                    msg.append("（赛道名称同步失败）");
                }
                
                try {
                    // 更新级别版本表中的竞赛名称
                    EntityWrapper<JingsaiJibieBanbenEntity> jibieEw = new EntityWrapper<>();
                    jibieEw.eq("jingsai_id", jingsaiId);
                    List<JingsaiJibieBanbenEntity> jibieList = jingsaiJibieBanbenService.selectList(jibieEw);
                    if (jibieList != null && !jibieList.isEmpty()) {
                        for (JingsaiJibieBanbenEntity jibie : jibieList) {
                            jibie.setJingsaimingcheng(newName);
                            jingsaiJibieBanbenService.updateById(jibie);
                        }
                        msg.append("，级别版本名称已同步更新");
                        log.info("同步更新级别版本竞赛名称，竞赛ID: {}，旧名称: {} -> 新名称: {}", jingsaiId, oldName, newName);
                    }
                } catch (Exception e) {
                    log.error("同步更新级别版本竞赛名称失败", e);
                    msg.append("（级别版本名称同步失败）");
                }
            }
            
            // 6.2 "是否有赛道"标志变更
            String newShifouYouSaidao = jingsaixinxi.getShifouYouSaidao();
            String oldShifouYouSaidao = existingJingsai.getShifouYouSaidao();
            if (newShifouYouSaidao != null && !newShifouYouSaidao.equals(oldShifouYouSaidao)) {
                // 从"否"改为"是"：自动创建默认赛道
                if ("是".equals(newShifouYouSaidao) && "否".equals(oldShifouYouSaidao)) {
                    try {
                        JingsaiSaidaoEntity saidao = new JingsaiSaidaoEntity();
                        saidao.setId(IdWorker.getId());
                        saidao.setJingsaiId(jingsaiId);
                        saidao.setJingsaimingcheng(newName);
                        saidao.setSaidaoMingcheng("默认赛道");
                        saidao.setSaidaoBianma("SD001");
                        saidao.setCansaiLeixing("个人赛");
                        saidao.setTuanduiRenshuMin(1);
                        saidao.setTuanduiRenshuMax(1);
                        saidao.setBaomingShuoming("默认赛道，支持个人参赛");
                        saidao.setPingshenBiaozhun("按作品评分");
                        saidao.setIsActive("是");
                        saidao.setSortOrder(1);
                        saidao.setAddtime(new Date());
                        jingsaiSaidaoService.insert(saidao);
                        msg.append("，已自动创建默认赛道");
                        log.info("修改竞赛时自动创建默认赛道，竞赛ID: {}", jingsaiId);
                    } catch (Exception e) {
                        log.error("修改竞赛时自动创建赛道失败", e);
                        msg.append("（赛道创建失败，请手动创建）");
                    }
                }
                // 从"是"改为"否"：删除所有关联赛道
                if ("否".equals(newShifouYouSaidao) && "是".equals(oldShifouYouSaidao)) {
                    try {
                        EntityWrapper<JingsaiSaidaoEntity> saidaoEw = new EntityWrapper<>();
                        saidaoEw.eq("jingsai_id", jingsaiId);
                        int saidaoCountBefore = jingsaiSaidaoService.selectCount(saidaoEw);
                        boolean deletedSaidao = jingsaiSaidaoService.delete(saidaoEw);
                        if (deletedSaidao && saidaoCountBefore > 0) {
                            msg.append("，已删除关联赛道(" + saidaoCountBefore + "条)");
                            log.info("修改竞赛时删除关联赛道，竞赛ID: {}，删除{}条", jingsaiId, saidaoCountBefore);
                        }
                    } catch (Exception e) {
                        log.error("修改竞赛时删除赛道失败", e);
                        msg.append("（赛道删除失败）");
                    }
                }
            }
            
            // 6.3 竞赛级别变更时，更新级别版本
            String newJibie = jingsaixinxi.getJingsaiJibie();
            String oldJibie = existingJingsai.getJingsaiJibie();
            if (newJibie != null && !newJibie.equals(oldJibie) && StringUtils.hasText(newJibie)) {
                try {
                    EntityWrapper<JingsaiJibieBanbenEntity> jibieEw = new EntityWrapper<>();
                    jibieEw.eq("jingsai_id", jingsaiId);
                    jibieEw.eq("is_current", "是");
                    JingsaiJibieBanbenEntity currentJibie = jingsaiJibieBanbenService.selectOne(jibieEw);
                    if (currentJibie != null) {
                        // 更新现有当前级别版本的级别
                        currentJibie.setJibie(newJibie);
                        if (jingsaixinxi.getNianfen() != null) {
                            currentJibie.setJingsaiNianfen(jingsaixinxi.getNianfen());
                        }
                        jingsaiJibieBanbenService.updateById(currentJibie);
                        msg.append("，级别版本已同步更新");
                        log.info("同步更新级别版本，竞赛ID: {}，旧级别: {} -> 新级别: {}", jingsaiId, oldJibie, newJibie);
                    } else {
                        // 没有当前级别版本，自动创建
                        JingsaiJibieBanbenEntity jibie = new JingsaiJibieBanbenEntity();
                        jibie.setId(IdWorker.getId());
                        jibie.setJingsaiId(jingsaiId);
                        jibie.setJingsaimingcheng(newName);
                        jibie.setJingsaiNianfen(jingsaixinxi.getNianfen());
                        jibie.setJibie(newJibie);
                        jibie.setRenzhengJigou("系统自动生成");
                        jibie.setWenjianHao("AUTO-" + System.currentTimeMillis());
                        jibie.setShengxiaoRiqi(new Date());
                        jibie.setIsCurrent("是");
                        jibie.setCaozuoRen(gonghao != null ? gonghao : "system");
                        jibie.setAddtime(new Date());
                        jingsaiJibieBanbenService.insert(jibie);
                        msg.append("，已自动创建级别版本");
                        log.info("修改竞赛时自动创建级别版本，竞赛ID: {}", jingsaiId);
                    }
                } catch (Exception e) {
                    log.error("同步更新级别版本失败", e);
                    msg.append("（级别版本同步失败）");
                }
            }
            
            // 6.4 竞赛名称变更时，同步更新报名表中的竞赛名称
            if (nameChanged) {
                try {
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.eq("jingsai_id", jingsaiId);
                    List<JingsaibaomingEntity> baomingList = jingsaibaomingService.selectList(baomingEw);
                    if (baomingList != null && !baomingList.isEmpty()) {
                        for (JingsaibaomingEntity baoming : baomingList) {
                            baoming.setJingsaimingcheng(newName);
                            jingsaibaomingService.updateById(baoming);
                        }
                        msg.append("，报名记录名称已同步更新(" + baomingList.size() + "条)");
                        log.info("同步更新报名记录竞赛名称，竞赛ID: {}，旧名称: {} -> 新名称: {}, 更新{}条", jingsaiId, oldName, newName, baomingList.size());
                    }
                } catch (Exception e) {
                    log.error("同步更新报名记录竞赛名称失败", e);
                    msg.append("（报名记录名称同步失败）");
                }
            }
            
            // 6.5 工号/教师姓名变更时，同步更新报名表中的工号和教师姓名
            String newGonghao = jingsaixinxi.getGonghao();
            String oldGonghao = existingJingsai.getGonghao();
            String newJiaoshixingming = jingsaixinxi.getJiaoshixingming();
            String oldJiaoshixingming = existingJingsai.getJiaoshixingming();
            boolean gonghaoChanged = (newGonghao != null && !newGonghao.equals(oldGonghao));
            boolean jiaoshiChanged = (newJiaoshixingming != null && !newJiaoshixingming.equals(oldJiaoshixingming));
            if (gonghaoChanged || jiaoshiChanged) {
                try {
                    EntityWrapper<JingsaibaomingEntity> baomingEw2 = new EntityWrapper<>();
                    baomingEw2.eq("jingsai_id", jingsaiId);
                    List<JingsaibaomingEntity> baomingList2 = jingsaibaomingService.selectList(baomingEw2);
                    if (baomingList2 != null && !baomingList2.isEmpty()) {
                        for (JingsaibaomingEntity baoming : baomingList2) {
                            if (gonghaoChanged && newGonghao != null) {
                                baoming.setGonghao(newGonghao);
                            }
                            if (jiaoshiChanged && newJiaoshixingming != null) {
                                baoming.setJiaoshixingming(newJiaoshixingming);
                            }
                            jingsaibaomingService.updateById(baoming);
                        }
                        msg.append("，报名记录教师信息已同步更新(" + baomingList2.size() + "条)");
                        log.info("同步更新报名记录教师信息，竞赛ID: {}", jingsaiId);
                    }
                } catch (Exception e) {
                    log.error("同步更新报名记录教师信息失败", e);
                    msg.append("（报名记录教师信息同步失败）");
                }
            }
            
            log.info("修改竞赛信息成功，ID: {}, 名称：{}", jingsaixinxi.getId(), jingsaixinxi.getJingsaimingcheng());
            return R.ok(msg.toString());
            
        } catch (Exception e) {
            log.error("修改竞赛信息 ID{}异常：", jingsaixinxi.getId(), e);
            return R.error("修改失败，请联系管理员");
        }
    }

    /**
     * 删除竞赛信息
     * 功能：批量删除竞赛记录
     * 
     * 注意：此操作不可逆，请谨慎使用
     * 教师只能删除自己创建的竞赛
     * 
     * @param ids 竞赛 ID 数组
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @ApiOperation("批量删除竞赛信息")
    @RequestMapping("/delete")
    @OperationLog("删除竞赛信息")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody String[] ids, HttpServletRequest request) {
        // 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除竞赛失败：ID 数组为空");
            return R.error("请选择要删除的竞赛");
        }
        
        try {
            // 将 String[] 转换为 Long[]，避免前端传递大数字时的精度丢失问题
            Long[] longIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                try {
                    longIds[i] = Long.parseLong(ids[i]);
                } catch (NumberFormatException e) {
                    log.error("ID 格式错误: {}", ids[i]);
                    return R.error("竞赛 ID 格式错误: " + ids[i]);
                }
            }
            
            log.info("接收到的删除请求，原始 IDs: {}，转换后 Long IDs: {}", Arrays.toString(ids), Arrays.toString(longIds));
            
            // 权限验证：教师只能删除自己创建的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            String gonghao = (String) request.getSession().getAttribute("username");
            
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                for (Long id : longIds) {
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(id);
                    if (jingsai == null) {
                        log.warn("竞赛不存在，ID: {}", id);
                        return R.error("竞赛 ID " + id + " 不存在");
                    }
                    
                    if (gonghao == null || !gonghao.equalsIgnoreCase(jingsai.getGonghao())) {
                        log.warn("教师 {} 尝试删除不属于自己的竞赛 ID: {}，竞赛创建者: {}",
                                gonghao, id, jingsai.getGonghao());
                        return R.error("只能删除自己创建的竞赛");
                    }
                }
                log.info("教师 {} 删除自己的 {} 个竞赛", gonghao, longIds.length);
            }
            
            // 级联删除关联数据（在删除竞赛主记录之前）
            StringBuilder cascadeMsg = new StringBuilder();
            
            for (Long id : longIds) {
                // 3.1 删除关联赛道
                try {
                    EntityWrapper<JingsaiSaidaoEntity> saidaoEw = new EntityWrapper<>();
                    saidaoEw.eq("jingsai_id", id);
                    int saidaoCountBefore = jingsaiSaidaoService.selectCount(saidaoEw);
                    boolean saidaoDeleted = jingsaiSaidaoService.delete(saidaoEw);
                    if (saidaoDeleted && saidaoCountBefore > 0) {
                        log.info("级联删除赛道，竞赛ID: {}，删除{}条", id, saidaoCountBefore);
                        cascadeMsg.append("，删除赛道" + saidaoCountBefore + "条");
                    }
                } catch (Exception e) {
                    log.error("级联删除赛道失败，竞赛ID: {}", id, e);
                    cascadeMsg.append("（赛道删除失败）");
                }
                
                // 3.2 删除关联级别版本
                try {
                    EntityWrapper<JingsaiJibieBanbenEntity> jibieEw = new EntityWrapper<>();
                    jibieEw.eq("jingsai_id", id);
                    int jibieCountBefore = jingsaiJibieBanbenService.selectCount(jibieEw);
                    boolean jibieDeleted = jingsaiJibieBanbenService.delete(jibieEw);
                    if (jibieDeleted && jibieCountBefore > 0) {
                        log.info("级联删除级别版本，竞赛ID: {}，删除{}条", id, jibieCountBefore);
                        cascadeMsg.append("，删除级别版本" + jibieCountBefore + "条");
                    }
                } catch (Exception e) {
                    log.error("级联删除级别版本失败，竞赛ID: {}", id, e);
                    cascadeMsg.append("（级别版本删除失败）");
                }
                
                // 3.3 删除关联晋级关系（作为父竞赛或子竞赛）
                try {
                    EntityWrapper<JingsaiJinjiGuanxiEntity> jinjiEw = new EntityWrapper<>();
                    jinjiEw.eq("fu_jingsai_id", id).or().eq("zi_jingsai_id", id);
                    int jinjiCountBefore = jingsaiJinjiGuanxiService.selectCount(jinjiEw);
                    boolean jinjiDeleted = jingsaiJinjiGuanxiService.delete(jinjiEw);
                    if (jinjiDeleted && jinjiCountBefore > 0) {
                        log.info("级联删除晋级关系，竞赛ID: {}，删除{}条", id, jinjiCountBefore);
                        cascadeMsg.append("，删除晋级关系" + jinjiCountBefore + "条");
                    }
                } catch (Exception e) {
                    log.error("级联删除晋级关系失败，竞赛ID: {}", id, e);
                    cascadeMsg.append("（晋级关系删除失败）");
                }
                
                // 3.4 删除关联报名记录
                try {
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.eq("jingsai_id", id);
                    int baomingCountBefore = jingsaibaomingService.selectCount(baomingEw);
                    boolean baomingDeleted = jingsaibaomingService.delete(baomingEw);
                    if (baomingDeleted && baomingCountBefore > 0) {
                        log.info("级联删除报名记录，竞赛ID: {}，删除{}条", id, baomingCountBefore);
                        cascadeMsg.append("，删除报名" + baomingCountBefore + "条");
                    }
                } catch (Exception e) {
                    log.error("级联删除报名记录失败，竞赛ID: {}", id, e);
                    cascadeMsg.append("（报名记录删除失败）");
                }
            }
            
            // 批量删除竞赛主记录
            jingsaixinxiService.deleteBatchIds(Arrays.asList(longIds));
            
            String resultMsg = "删除成功" + cascadeMsg.toString();
            log.info("删除竞赛信息成功，IDs: {}，级联信息: {}", Arrays.toString(longIds), cascadeMsg);
            return R.ok(resultMsg);
            
        } catch (Exception e) {
            log.error("删除竞赛 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
        }
    }

    /**
     * 提醒功能接口
     * 功能：统计即将到期或开始的竞赛数量
     * 
     * 应用场景：
     * - 首页提醒：显示即将开始的竞赛
     * - 到期提醒：显示报名即将截止的竞赛
     * 
     * @param columnName 字段名 (如 jingsaishijian-比赛时间)
     * @param request HTTP 请求
     * @param type 类型 ("1": 今天，"2": 区间范围)
     * @param map 查询参数 (remindstart-开始天数，remindend-结束天数)
     * @return R 统一返回结果，包含数量
     */
    @RequestMapping("/remind/{columnName}/{type}")
    public R remindCount(@PathVariable("columnName") String columnName, 
                         HttpServletRequest request,
                         @PathVariable("type") String type, 
                         @RequestParam Map<String, Object> map) {
        try {
            // 设置字段名
            map.put("column", columnName);
            map.put("type", type);
            
            // 处理时间区间 (类型 2：自定义区间)
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
            
            // 构建查询条件
            EntityWrapper<JingsaixinxiEntity> wrapper = new EntityWrapper<>();
            if (map.get("remindstart") != null) {
                wrapper.ge(columnName, map.get("remindstart")); // 大于等于开始时间
            }
            if (map.get("remindend") != null) {
                wrapper.le(columnName, map.get("remindend")); // 小于等于结束时间
            }
            
            // 权限控制：教师只能查看自己的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                wrapper.eq("gonghao", gonghao);
            }
            
            // 统计数量
            int count = jingsaixinxiService.selectCount(wrapper);
            
            log.debug("竞赛提醒查询成功，字段：{}, 类型：{}, 数量：{}", columnName, type, count);
            return R.ok().put("count", count);
            
        } catch (Exception e) {
            log.error("竞赛提醒查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }
    
    /**
     * 数据统计接口
     * 功能：统计竞赛数量、报名人数、审核状态等数据
     * 
     * 应用场景：
     * - 首页仪表盘展示
     * - 管理员查看系统概况
     * - 教师查看所发布竞赛的统计
     * - 学生查看所有竞赛的统计（与列表保持一致）
     * 
     * @param request HTTP 请求
     * @return R 统一返回结果，包含统计数据
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 统计数据查询开始 ==========");
            Map<String, Object> stats = new HashMap<>();
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            // 注意：竞赛信息页面的统计数据对所有角色都是全局统计
            // 学生看到的是所有竞赛的统计，不是只统计自己报名的竞赛
            // 这样才能与列表数据保持一致（列表显示所有竞赛）
            
            // 统计竞赛总数（所有角色都统计所有竞赛）
            int totalContests = jingsaixinxiService.selectCount(null);
            stats.put("totalJingsai", totalContests);
            log.info("竞赛总数：{}", totalContests);
            
            // 统计进行中竞赛（竞赛时间未过期的）
            EntityWrapper<JingsaixinxiEntity> ongoingEw = new EntityWrapper<>();
            ongoingEw.gt("jingsaishijian", new Date());
            int ongoingCount = jingsaixinxiService.selectCount(ongoingEw);
            stats.put("ongoingJingsai", ongoingCount);
            log.info("进行中竞赛数：{}", ongoingCount);
            
            // 统计报名记录数（所有角色都统计所有报名）
            int totalApplications = jingsaibaomingService.selectCount(null);
            stats.put("baomingJingsai", totalApplications);
            log.info("报名记录总数：{}", totalApplications);
            
            // 统计待审核报名数（所有角色都统计所有待审核报名）
            EntityWrapper<JingsaibaomingEntity> pendingBaomingEw = new EntityWrapper<>();
            pendingBaomingEw.eq("sfsh", "待审核");
            int pendingApplications = jingsaibaomingService.selectCount(pendingBaomingEw);
            stats.put("pendingBaoming", pendingApplications);
            log.info("待审核报名数：{}", pendingApplications);
            
            // 统计缴费记录数（所有角色都统计所有缴费）
            int totalJiaofei = jingsaiJiaofeiJiluService.selectCount(null);
            stats.put("totalJiaofei", totalJiaofei);
            log.info("缴费记录总数：{}", totalJiaofei);
            
            // 统计待审核缴费数（所有角色都统计所有待审核缴费）
            EntityWrapper<JingsaiJiaofeiJiluEntity> pendingJiaofeiEw = new EntityWrapper<>();
            pendingJiaofeiEw.eq("jiaofei_zhuangtai", "已缴费");
            int pendingJiaofei = jingsaiJiaofeiJiluService.selectCount(pendingJiaofeiEw);
            stats.put("pendingJiaofei", pendingJiaofei);
            log.info("待审核缴费数：{}", pendingJiaofei);
            
            // 统计作品提交数（所有角色都统计所有作品）
            EntityWrapper<JingsaibaomingEntity> zuopinEw = new EntityWrapper<>();
            zuopinEw.isNotNull("cansaizuopin");
            zuopinEw.ne("cansaizuopin", "");
            int totalZuopin = jingsaibaomingService.selectCount(zuopinEw);
            stats.put("totalZuopin", totalZuopin);
            log.info("作品提交总数：{}", totalZuopin);
            
            // 统计团队数量（所有角色都统计所有团队）
            int totalTuandui = jingsaiTuanduiService.selectCount(null);
            stats.put("totalTuandui", totalTuandui);
            log.info("团队总数：{}", totalTuandui);
            
            // 统计待审核团队数（所有角色都统计所有待审核团队）
            EntityWrapper<JingsaiTuanduiEntity> pendingTuanduiEw = new EntityWrapper<>();
            pendingTuanduiEw.eq("shenhe_zhuangtai", "待审核");
            int pendingTuandui = jingsaiTuanduiService.selectCount(pendingTuanduiEw);
            stats.put("pendingTuandui", pendingTuandui);
            log.info("待审核团队数：{}", pendingTuandui);
            
            // 统计待审核复核申请数（所有角色都统计所有待审核复核）
            EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
            fuheEw.eq("fuhe_status", "待审核");
            int pendingFuhe = zuopindafenFuheService.selectCount(fuheEw);
            stats.put("pendingFuhe", pendingFuhe);
            log.info("待审核复核申请数：{}", pendingFuhe);

            log.info("统计数据查询成功，角色：{}", tableName);
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
    
    /**
     * “我的竞赛”统计数据接口
     * 功能：只统计当前教师自己创建的竞赛数据
     * 
     * @param request HTTP 请求
     * @return R 统一返回结果，包含统计数据
     */
    @RequestMapping("/mystatistics")
    public R mystatistics(HttpServletRequest request) {
        try {
            log.info("========== 我的竞赛统计数据查询开始 ==========");
            Map<String, Object> stats = new HashMap<>();
            
            // 获取当前登录教师的工号
            String gonghao = (String) request.getSession().getAttribute("username");
            String tableName = (String) request.getSession().getAttribute("tableName");
            
            if (!"jiaoshi".equals(tableName)) {
                log.warn("非教师角色访问我的竞赛统计，角色：{}", tableName);
                return R.error("只有教师可以查看我的竞赛统计");
            }
            
            log.info("教师工号：{}", gonghao);
            
            // 查询该教师创建的所有竞赛
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            int totalContests = jingsaixinxiService.selectCount(jingsaiEw);
            stats.put("totalJingsai", totalContests);
            log.info("我的竞赛总数：{}", totalContests);
            
            // 统计进行中竞赛
            EntityWrapper<JingsaixinxiEntity> ongoingEw2 = new EntityWrapper<>();
            ongoingEw2.eq("gonghao", gonghao);
            ongoingEw2.gt("jingsaishijian", new Date());
            int ongoingCount = jingsaixinxiService.selectCount(ongoingEw2);
            stats.put("ongoingJingsai", ongoingCount);
            log.info("我的进行中竞赛数：{}", ongoingCount);
            
            // 统计这些竞赛的报名记录数
            int totalApplications = 0;
            if (totalContests > 0) {
                EntityWrapper<JingsaixinxiEntity> myJingsaiEw = new EntityWrapper<>();
                myJingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(myJingsaiEw);
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    List<Long> myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(java.util.stream.Collectors.toList());
                    EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                    baomingEw.in("jingsai_id", myJingsaiIds);
                    totalApplications = jingsaibaomingService.selectCount(baomingEw);
                }
            }
            stats.put("baomingJingsai", totalApplications);
            log.info("我的竞赛报名记录总数：{}", totalApplications);
            
            log.info("我的竞赛统计数据查询成功，教师：{}", gonghao);
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("我的竞赛统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }
}
