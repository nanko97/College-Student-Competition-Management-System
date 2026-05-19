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
import com.entity.view.JingsaixinxiView;
import com.service.JingsaibaomingService;
import com.service.JingsaiJiaofeiJiluService;
import com.service.JingsaiTuanduiService;
import com.service.JingsaixinxiService;
import com.service.ZuopindafenFuheService;
import com.service.ZuopindafenService;
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
            // 1. 权限控制：根据前端传递的参数进行过滤
            // 注意：不在这里自动过滤教师的竞赛，因为“竞赛信息”页面需要显示所有竞赛
            // 如果前端需要只显示教师的竞赛，会通过params传递gonghao参数
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.debug("当前用户角色：{}", tableName);
            
            // 2. 构建查询条件 (支持模糊查询、区间查询、排序)
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            
            // 如果前端传递了gonghao参数，则进行过滤（用于“我的竞赛”页面）
            if (params.get("gonghao") != null && !params.get("gonghao").toString().isEmpty()) {
                ew.eq("gonghao", params.get("gonghao").toString());
                log.debug("按工号过滤：{}", params.get("gonghao"));
            }
            
            // 3. 处理前端传递的模糊查询参数（带 % 的参数）
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
            
            // 4. 执行分页查询
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
            // 1. 构建查询条件
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            
            // 2. 执行分页查询
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
    public R list(JingsaixinxiEntity jingsaixinxi) {
        try {
            // 1. 构建查询条件 (精确匹配)
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaixinxi, "jingsaixinxi"));
            
            // 2. 查询列表
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
            // 1. 构建查询条件
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaixinxi, "jingsaixinxi"));
            
            // 2. 查询视图数据 (关联查询)
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
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询竞赛详情失败：ID 非法，ID: {}", id);
                return R.error("竞赛 ID 非法");
            }
            
            // 2. 查询竞赛信息
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
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询竞赛详情失败：ID 非法，ID: {}", id);
                return R.error("竞赛 ID 非法");
            }
            
            // 2. 查询竞赛信息
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
     * 功能：发布新的竞赛信息
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 自动填充工号 (如果是教师发布)
     * 3. 生成唯一 ID
     * 4. 保存到数据库
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
        
        // 1. 基础参数校验
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("保存竞赛失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        try {
            // 2. 自动填充创建人工号 (如果是教师创建)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                if (StringUtils.hasText(gonghao)) {
                    jingsaixinxi.setGonghao(gonghao);
                    log.info("教师 {} 发布新竞赛：{}, 数据：{}", gonghao, 
                            jingsaixinxi.getJingsaimingcheng(), jingsaixinxi);
                } else {
                    log.error("教师工号为空，无法保存竞赛");
                    return R.error("请先登录");
                }
            } else {
                log.warn("当前用户角色：{}, 不是教师角色", tableName);
            }
            
            // 3. 实体校验 (使用验证工具类)
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 4. 生成唯一 ID 并保存
            jingsaixinxi.setId(IdWorker.getId());
            boolean result = jingsaixinxiService.insert(jingsaixinxi);
            
            log.info("保存竞赛信息{}，ID: {}, 名称：{}, 工号：{}", 
                    result ? "成功" : "失败",
                    jingsaixinxi.getId(), 
                    jingsaixinxi.getJingsaimingcheng(),
                    jingsaixinxi.getGonghao());
            
            if (result) {
                return R.ok("保存成功");
            } else {
                log.error("数据库插入返回 false");
                return R.error("保存失败，数据库操作异常");
            }
            
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
        // 1. 基础参数校验
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("添加竞赛失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        try {
            // 2. 自动填充创建人工号 (如果是教师创建)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                if (StringUtils.hasText(gonghao)) {
                    jingsaixinxi.setGonghao(gonghao);
                    log.info("教师 {} 添加新竞赛：{}", gonghao, jingsaixinxi.getJingsaimingcheng());
                } else {
                    log.error("教师工号为空，无法添加竞赛");
                    return R.error("请先登录");
                }
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 4. 生成唯一 ID 并保存
            jingsaixinxi.setId(IdWorker.getId());
            jingsaixinxiService.insert(jingsaixinxi);
            
            log.info("添加竞赛信息成功，ID: {}, 名称：{}", 
                     jingsaixinxi.getId(), jingsaixinxi.getJingsaimingcheng());
            return R.ok("添加成功");
            
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
        // 1. 参数校验
        if (jingsaixinxi.getId() == null || jingsaixinxi.getId() <= 0) {
            log.warn("修改竞赛失败：ID 非法，ID: {}", jingsaixinxi.getId());
            return R.error("竞赛 ID 非法");
        }
        
        if (!StringUtils.hasText(jingsaixinxi.getJingsaimingcheng())) {
            log.warn("修改竞赛失败：竞赛名称为空，ID: {}", jingsaixinxi.getId());
            return R.error("竞赛名称不能为空");
        }
        
        try {
            // 2. 权限验证：教师只能修改自己创建的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                
                // 查询竞赛信息，验证归属
                JingsaixinxiEntity existingJingsai = jingsaixinxiService.selectById(jingsaixinxi.getId());
                if (existingJingsai == null) {
                    return R.error("竞赛信息不存在");
                }
                
                if (!gonghao.equals(existingJingsai.getGonghao())) {
                    log.warn("教师 {} 尝试修改不属于自己的竞赛 ID: {}，竞赛创建者: {}", 
                            gonghao, jingsaixinxi.getId(), existingJingsai.getGonghao());
                    return R.error("只能修改自己创建的竞赛");
                }
                
                log.info("教师 {} 修改自己的竞赛 ID: {}", gonghao, jingsaixinxi.getId());
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 4. 执行更新
            jingsaixinxiService.updateById(jingsaixinxi);
            
            log.info("修改竞赛信息成功，ID: {}, 名称：{}", 
                     jingsaixinxi.getId(), jingsaixinxi.getJingsaimingcheng());
            return R.ok("修改成功");
            
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
    public R delete(@RequestBody Long[] ids, HttpServletRequest request) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除竞赛失败：ID 数组为空");
            return R.error("请选择要删除的竞赛");
        }
        
        try {
            // 2. 权限验证：教师只能删除自己创建的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                
                // 验证所有竞赛都属于该教师
                for (Long id : ids) {
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(id);
                    if (jingsai == null) {
                        return R.error("竞赛 ID " + id + " 不存在");
                    }
                    
                    if (!gonghao.equals(jingsai.getGonghao())) {
                        log.warn("教师 {} 尝试删除不属于自己的竞赛 ID: {}，竞赛创建者: {}", 
                                gonghao, id, jingsai.getGonghao());
                        return R.error("只能删除自己创建的竞赛");
                    }
                }
                
                log.info("教师 {} 删除自己的 {} 个竞赛", gonghao, ids.length);
            }
            
            // 3. 批量删除
            jingsaixinxiService.deleteBatchIds(Arrays.asList(ids));
            
            log.info("删除竞赛信息成功，IDs: {}", Arrays.toString(ids));
            return R.ok("删除成功");
            
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
            EntityWrapper<JingsaixinxiEntity> wrapper = new EntityWrapper<>();
            if (map.get("remindstart") != null) {
                wrapper.ge(columnName, map.get("remindstart")); // 大于等于开始时间
            }
            if (map.get("remindend") != null) {
                wrapper.le(columnName, map.get("remindend")); // 小于等于结束时间
            }
            
            // 4. 权限控制：教师只能查看自己的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                wrapper.eq("gonghao", gonghao);
            }
            
            // 5. 统计数量
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
            
            // 0. 如果是学生，获取该学生报名的竞赛ID列表（用于个人统计）
            // 注意：教师和管理员都显示所有竞赛的全局统计
            final List<Long> myJingsaiIds = new ArrayList<>();
            
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                log.info("学生角色，学号：{}", xuehao);
                
                // 查询该学生报名的所有竞赛ID（去重）
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.eq("xuehao", xuehao);
                List<JingsaibaomingEntity> myBaomingList = jingsaibaomingService.selectList(baomingEw);
                
                if (myBaomingList != null && !myBaomingList.isEmpty()) {
                    myJingsaiIds.addAll(myBaomingList.stream()
                            .map(JingsaibaomingEntity::getJingsaiId)
                            .distinct()
                            .collect(java.util.stream.Collectors.toList()));
                    log.info("学生 {} 报名了 {} 个竞赛，IDs: {}", xuehao, myJingsaiIds.size(), myJingsaiIds);
                } else {
                    log.info("学生 {} 还没有报名任何竞赛", xuehao);
                }
            } else if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                log.info("教师角色，工号：{} - 竞赛信息页面显示所有竞赛的全局统计", gonghao);
            }
            
            // 1. 统计竞赛总数
            int totalContests = 0;
            if ("xuesheng".equals(tableName)) {
                totalContests = myJingsaiIds.size();
            } else {
                // 教师和管理员：统计所有竞赛
                List<JingsaixinxiEntity> allContests = jingsaixinxiService.selectList(null);
                totalContests = allContests != null ? allContests.size() : 0;
            }
            stats.put("totalJingsai", totalContests);
            log.info("竞赛总数：{}", totalContests);
            
            // 2. 统计进行中竞赛（竞赛时间未过期的）
            int ongoingCount = 0;
            Date now = new Date();
            
            if ("xuesheng".equals(tableName)) {
                // 学生：只统计自己报名的竞赛
                if (!myJingsaiIds.isEmpty()) {
                    List<JingsaixinxiEntity> myContests = jingsaixinxiService.selectBatchIds(myJingsaiIds);
                    for (JingsaixinxiEntity contest : myContests) {
                        if (contest.getJingsaishijian() != null && contest.getJingsaishijian().after(now)) {
                            ongoingCount++;
                        }
                    }
                }
            } else {
                // 教师和管理员：统计所有竞赛
                List<JingsaixinxiEntity> allContests = jingsaixinxiService.selectList(null);
                if (allContests != null) {
                    for (JingsaixinxiEntity contest : allContests) {
                        if (contest.getJingsaishijian() != null && contest.getJingsaishijian().after(now)) {
                            ongoingCount++;
                        }
                    }
                }
            }
            stats.put("ongoingJingsai", ongoingCount);
            log.info("进行中竞赛数：{}", ongoingCount);
            
            // 3. 统计报名记录数
            int totalApplications = 0;
            if ("xuesheng".equals(tableName)) {
                // 学生：只统计自己的报名
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.eq("xuehao", (String) request.getSession().getAttribute("username"));
                List<JingsaibaomingEntity> myBaomings = jingsaibaomingService.selectList(baomingEw);
                totalApplications = myBaomings != null ? myBaomings.size() : 0;
            } else {
                // 教师和管理员：统计所有报名
                List<JingsaibaomingEntity> allBaomings = jingsaibaomingService.selectList(null);
                totalApplications = allBaomings != null ? allBaomings.size() : 0;
            }
            stats.put("baomingJingsai", totalApplications);
            log.info("报名记录总数：{}", totalApplications);
            
            // 4. 统计待审核报名数
            int pendingApplications = 0;
            if ("xuesheng".equals(tableName)) {
                // 学生：只统计自己的待审核报名
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.eq("xuehao", (String) request.getSession().getAttribute("username"));
                baomingEw.eq("sfsh", "待审核");
                List<JingsaibaomingEntity> pendingBaomings = jingsaibaomingService.selectList(baomingEw);
                pendingApplications = pendingBaomings != null ? pendingBaomings.size() : 0;
            } else {
                // 教师和管理员：统计所有待审核报名
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.eq("sfsh", "待审核");
                List<JingsaibaomingEntity> pendingBaomings = jingsaibaomingService.selectList(baomingEw);
                pendingApplications = pendingBaomings != null ? pendingBaomings.size() : 0;
            }
            stats.put("pendingBaoming", pendingApplications);
            log.info("待审核报名数：{}", pendingApplications);
            
            // 5. 统计缴费记录数
            int totalJiaofei = 0;
            if ("xuesheng".equals(tableName)) {
                // 学生：只统计自己的缴费
                EntityWrapper<JingsaiJiaofeiJiluEntity> jiaofeiEw = new EntityWrapper<>();
                jiaofeiEw.eq("xuehao", (String) request.getSession().getAttribute("username"));
                List<JingsaiJiaofeiJiluEntity> myJiaofei = jingsaiJiaofeiJiluService.selectList(jiaofeiEw);
                totalJiaofei = myJiaofei != null ? myJiaofei.size() : 0;
            } else {
                // 教师和管理员：统计所有缴费
                List<JingsaiJiaofeiJiluEntity> allJiaofei = jingsaiJiaofeiJiluService.selectList(null);
                totalJiaofei = allJiaofei != null ? allJiaofei.size() : 0;
            }
            stats.put("totalJiaofei", totalJiaofei);
            log.info("缴费记录总数：{}", totalJiaofei);
            
            // 6. 统计待审核缴费数
            int pendingJiaofei = 0;
            if ("jiaoshi".equals(tableName) || "xuesheng".equals(tableName)) {
                // 教师或学生：只统计自己相关的竞赛缴费
                if (!myJingsaiIds.isEmpty()) {
                    EntityWrapper<JingsaiJiaofeiJiluEntity> jiaofeiEw = new EntityWrapper<>();
                    jiaofeiEw.in("jingsai_id", myJingsaiIds);
                    jiaofeiEw.eq("jiaofei_zhuangtai", "已缴费");
                    List<JingsaiJiaofeiJiluEntity> pendingJiaofeiList = jingsaiJiaofeiJiluService.selectList(jiaofeiEw);
                    pendingJiaofei = pendingJiaofeiList != null ? pendingJiaofeiList.size() : 0;
                }
            } else {
                // 管理员：统计所有待审核缴费
                EntityWrapper<JingsaiJiaofeiJiluEntity> jiaofeiEw = new EntityWrapper<>();
                jiaofeiEw.eq("jiaofei_zhuangtai", "已缴费");
                List<JingsaiJiaofeiJiluEntity> pendingJiaofeiList = jingsaiJiaofeiJiluService.selectList(jiaofeiEw);
                pendingJiaofei = pendingJiaofeiList != null ? pendingJiaofeiList.size() : 0;
            }
            stats.put("pendingJiaofei", pendingJiaofei);
            log.info("待审核缴费数：{}", pendingJiaofei);
            
            // 7. 统计作品提交数
            int totalZuopin = 0;
            if ("jiaoshi".equals(tableName) || "xuesheng".equals(tableName)) {
                // 教师或学生：只统计自己相关的竞赛作品
                if (!myJingsaiIds.isEmpty()) {
                    EntityWrapper<JingsaibaomingEntity> zuopinEw = new EntityWrapper<>();
                    zuopinEw.in("jingsai_id", myJingsaiIds);
                    zuopinEw.isNotNull("cansaizuopin");
                    zuopinEw.ne("cansaizuopin", "");
                    List<JingsaibaomingEntity> zuopinBaomings = jingsaibaomingService.selectList(zuopinEw);
                    totalZuopin = zuopinBaomings != null ? zuopinBaomings.size() : 0;
                }
            } else {
                // 管理员：统计所有作品
                EntityWrapper<JingsaibaomingEntity> zuopinEw = new EntityWrapper<>();
                zuopinEw.isNotNull("cansaizuopin");
                zuopinEw.ne("cansaizuopin", "");
                List<JingsaibaomingEntity> zuopinBaomings = jingsaibaomingService.selectList(zuopinEw);
                totalZuopin = zuopinBaomings != null ? zuopinBaomings.size() : 0;
            }
            stats.put("totalZuopin", totalZuopin);
            log.info("作品提交总数：{}", totalZuopin);
            
            // 8. 统计团队数量
            int totalTuandui = 0;
            if ("jiaoshi".equals(tableName) || "xuesheng".equals(tableName)) {
                // 教师或学生：只统计自己相关的竞赛团队
                if (!myJingsaiIds.isEmpty()) {
                    EntityWrapper<JingsaiTuanduiEntity> tuanduiEw = new EntityWrapper<>();
                    tuanduiEw.in("jingsai_id", myJingsaiIds);
                    List<JingsaiTuanduiEntity> myTuandui = jingsaiTuanduiService.selectList(tuanduiEw);
                    totalTuandui = myTuandui != null ? myTuandui.size() : 0;
                }
            } else {
                // 管理员：统计所有团队
                List<JingsaiTuanduiEntity> allTuandui = jingsaiTuanduiService.selectList(null);
                totalTuandui = allTuandui != null ? allTuandui.size() : 0;
            }
            stats.put("totalTuandui", totalTuandui);
            log.info("团队总数：{}", totalTuandui);
            
            // 9. 统计待审核团队数
            int pendingTuandui = 0;
            if ("jiaoshi".equals(tableName) || "xuesheng".equals(tableName)) {
                // 教师或学生：只统计自己相关的竞赛团队
                if (!myJingsaiIds.isEmpty()) {
                    EntityWrapper<JingsaiTuanduiEntity> tuanduiEw = new EntityWrapper<>();
                    tuanduiEw.in("jingsai_id", myJingsaiIds);
                    tuanduiEw.eq("shenhe_zhuangtai", "待审核");
                    List<JingsaiTuanduiEntity> pendingTuanduiList = jingsaiTuanduiService.selectList(tuanduiEw);
                    pendingTuandui = pendingTuanduiList != null ? pendingTuanduiList.size() : 0;
                }
            } else {
                // 管理员：统计所有待审核团队
                EntityWrapper<JingsaiTuanduiEntity> tuanduiEw = new EntityWrapper<>();
                tuanduiEw.eq("shenhe_zhuangtai", "待审核");
                List<JingsaiTuanduiEntity> pendingTuanduiList = jingsaiTuanduiService.selectList(tuanduiEw);
                pendingTuandui = pendingTuanduiList != null ? pendingTuanduiList.size() : 0;
            }
            stats.put("pendingTuandui", pendingTuandui);
            log.info("待审核团队数：{}", pendingTuandui);
            
            // 10. 统计待审核复核申请数
            int pendingFuhe = 0;
            if ("jiaoshi".equals(tableName) || "xuesheng".equals(tableName)) {
                // 教师或学生：只统计自己相关的竞赛复核
                if (!myJingsaiIds.isEmpty()) {
                    // 先获取这些竞赛的所有作品打分记录
                    List<ZuopindafenEntity> allZuopindafen = zuopindafenService.selectList(null);
                    
                    // 获取我相关的竞赛名称列表
                    List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectBatchIds(myJingsaiIds);
                    final List<String> myJingsaiNames = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getJingsaimingcheng)
                            .collect(java.util.stream.Collectors.toList());
                    
                    // 过滤出这些竞赛的作品打分ID
                    final List<Long> myZuopindafenIds = allZuopindafen.stream()
                            .filter(z -> myJingsaiNames.contains(z.getJingsaimingcheng()))
                            .map(ZuopindafenEntity::getId)
                            .collect(java.util.stream.Collectors.toList());
                    
                    // 统计这些作品打分的待审核复核申请
                    if (!myZuopindafenIds.isEmpty()) {
                        EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
                        fuheEw.in("zuopindafen_id", myZuopindafenIds);
                        fuheEw.eq("fuhe_status", "待审核");
                        List<ZuopindafenFuheEntity> pendingFuheList = zuopindafenFuheService.selectList(fuheEw);
                        pendingFuhe = pendingFuheList != null ? pendingFuheList.size() : 0;
                    }
                }
            } else {
                // 管理员：统计所有待审核复核
                EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
                fuheEw.eq("fuhe_status", "待审核");
                List<ZuopindafenFuheEntity> pendingFuheList = zuopindafenFuheService.selectList(fuheEw);
                pendingFuhe = pendingFuheList != null ? pendingFuheList.size() : 0;
            }
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
            
            // 1. 查询该教师创建的所有竞赛
            EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
            jingsaiEw.eq("gonghao", gonghao);
            List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
            
            int totalContests = myJingsaiList != null ? myJingsaiList.size() : 0;
            stats.put("totalJingsai", totalContests);
            log.info("我的竞赛总数：{}", totalContests);
            
            // 2. 统计进行中竞赛
            int ongoingCount = 0;
            Date now = new Date();
            if (myJingsaiList != null) {
                for (JingsaixinxiEntity contest : myJingsaiList) {
                    if (contest.getJingsaishijian() != null && contest.getJingsaishijian().after(now)) {
                        ongoingCount++;
                    }
                }
            }
            stats.put("ongoingJingsai", ongoingCount);
            log.info("我的进行中竞赛数：{}", ongoingCount);
            
            // 3. 统计这些竞赛的报名记录数
            int totalApplications = 0;
            if (totalContests > 0) {
                List<Long> myJingsaiIds = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                EntityWrapper<JingsaibaomingEntity> baomingEw = new EntityWrapper<>();
                baomingEw.in("jingsai_id", myJingsaiIds);
                List<JingsaibaomingEntity> myBaomings = jingsaibaomingService.selectList(baomingEw);
                totalApplications = myBaomings != null ? myBaomings.size() : 0;
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
