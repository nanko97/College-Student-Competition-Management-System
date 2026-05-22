package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaiFeiyongEntity;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaibaomingView;
import com.service.JingsaibaomingService;
import com.service.JingsaiFeiyongService;
import com.service.JingsaixinxiService;
import com.utils.EntityUtil;
import com.utils.IdWorker;
import com.utils.MPUtil;
import com.utils.PageUtils;
import com.utils.R;
import com.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 竞赛报名管理控制器
 * 功能：学生报名竞赛、教师审核报名、报名信息管理
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
@RequestMapping("/jingsaibaoming")
@Slf4j // 日志注解，便于问题排查
public class JingsaibaomingController {
    
    @Autowired
    private JingsaibaomingService jingsaibaomingService;
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;
    
    @Autowired
    private JingsaiFeiyongService jingsaiFeiyongService;
    
    @Autowired
    private com.service.JingsaiJiaofeiJiluService jiaofeiJiluService;
    
    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    @Autowired
    private com.service.XueshengService xueshengService;

    /**
     * 后端分页列表查询
     * 功能：带权限控制的报名信息查询
     * 
     * 权限逻辑：
     * - 教师角色：只能查看自己发布的竞赛的报名
     * - 学生角色：只能查看自己的报名
     * - 管理员：可以查看所有报名信息
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jingsaibaoming 报名信息实体 (用于条件查询)
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, 
                  JingsaibaomingEntity jingsaibaoming,
                  HttpServletRequest request) {
        log.info("=========== 报名分页查询开始 ===========");
        log.info("请求参数：{}", params);
        log.info("jingsaibaoming对象：{}", jingsaibaoming);
        
        try {
            // 1. 权限控制：根据用户角色和前端参数过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            log.info("前端传递的xuehao：{}", params.get("xuehao"));
            
            // 如果前端传递了gonghao参数，则进行过滤（用于"我的报名"页面，教师只看自己竞赛的报名）
            if (params.get("gonghao") != null && !params.get("gonghao").toString().isEmpty()) {
                String gonghao = params.get("gonghao").toString();
                log.info("按工号 {} 过滤报名数据", gonghao);
                                        
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                                        
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    // 提取竞赛 ID 列表
                    List<Long> myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(Collectors.toList());
                    // 使用 IN 查询，只查询这些竞赛的报名
                    params.put("jingsai_id_in", myJingsaiIds);
                    log.info("教师 {} 查询自己组织的 {} 个竞赛的报名列表", gonghao, myJingsaiIds.size());
                } else {
                    // 该教师没有创建任何竞赛，返回空结果
                    params.put("jingsai_id", -1);
                    log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                }
            } else if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的报名 - 强制过滤
                String xuehao = (String) request.getSession().getAttribute("username");
                log.info("从Session获取的username：{}", xuehao);
                // 优先使用session中的学号，确保权限安全
                if (xuehao != null && !xuehao.isEmpty()) {
                    params.put("xuehao", xuehao); // 强制覆盖前端参数
                    jingsaibaoming.setXuehao(xuehao);
                    log.info("学生权限过滤：只查询学号 {} 的报名记录", xuehao);
                } else {
                    log.warn("Session中没有username，无法进行权限过滤！");
                }
            } else if ("jiaoshi".equals(tableName)) {
                // 教师可以查看所有竞赛的报名信息（报名管理页面需要显示所有竞赛的报名）
                // 如果前端传递了gonghao参数，则按工号过滤（用于"我的报名"等页面）
                log.info("教师用户查看报名管理，不做自动过滤，显示所有竞赛的报名信息");
            }
            // 管理员（adminusers）能看到全部数据，不进行权限过滤
            
            log.info("最终jingsaibaoming对象：{}", jingsaibaoming);
            log.info("最终params：{}", params);
            
            // 2. 构建查询条件
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            
            // 处理jingsai_id=-1的情况（教师没有创建任何竞赛，返回空列表）
            if (params.containsKey("jingsai_id") && params.get("jingsai_id") != null) {
                Object jingsaiId = params.get("jingsai_id");
                if (jingsaiId instanceof Integer && (Integer)jingsaiId == -1) {
                    ew.eq("id", -1);  // 设置不可能的条件，返回空列表
                    log.debug("教师没有创建任何竞赛，返回空列表");
                }
            }
            
            // 处理jingsai_id_in参数（用于教师查看自己竞赛的报名）
            if (params.containsKey("jingsai_id_in") && params.get("jingsai_id_in") != null) {
                @SuppressWarnings("unchecked")
                java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
                if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                    ew.in("jingsai_id", jingsaiIds);
                    log.debug("按竞赛ID列表过滤：{}", jingsaiIds);
                }
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
                // 竞赛类型需要关联查询jingsaixinxi表
                if (value.contains("%")) {
                    // 模糊查询：先查询匹配的竞赛 ID 列表
                    String keyword = value.replace("%", "");
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.like("jingsaileixing", keyword);
                    List<JingsaixinxiEntity> jingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                    
                    if (jingsaiList != null && !jingsaiList.isEmpty()) {
                        // 提取竞赛 ID 列表
                        List<Long> jingsaiIds = jingsaiList.stream()
                                .map(JingsaixinxiEntity::getId)
                                .collect(Collectors.toList());
                        // 使用 IN 查询
                        ew.in("jingsai_id", jingsaiIds);
                        log.debug("竞赛类型模糊查询：{}，匹配 {} 个竞赛", keyword, jingsaiIds.size());
                    } else {
                        // 没有匹配的竞赛，返回空结果
                        ew.eq("id", -1);
                    }
                } else {
                    // 精确查询：先查询匹配的竞赛 ID 列表
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.eq("jingsaileixing", value);
                    List<JingsaixinxiEntity> jingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                    
                    if (jingsaiList != null && !jingsaiList.isEmpty()) {
                        List<Long> jingsaiIds = jingsaiList.stream()
                                .map(JingsaixinxiEntity::getId)
                                .collect(Collectors.toList());
                        ew.in("jingsai_id", jingsaiIds);
                        log.debug("竞赛类型精确查询：{}，匹配 {} 个竞赛", value, jingsaiIds.size());
                    } else {
                        ew.eq("id", -1);
                    }
                }
            }
            // 学号过滤（所有角色都需要过滤）
            if (params.get("xuehao") != null) {
                String value = params.get("xuehao").toString();
                if (value.contains("%")) {
                    ew.like("xuehao", value.replace("%", ""));
                } else {
                    ew.eq("xuehao", value);
                }
            }
            if (params.get("xueshengxingming") != null) {
                String value = params.get("xueshengxingming").toString();
                if (value.contains("%")) {
                    ew.like("xueshengxingming", value.replace("%", ""));
                } else {
                    ew.eq("xueshengxingming", value);
                }
            }
            // 审核状态过滤（sfsh字段）
            if (params.get("sfsh") != null && !params.get("sfsh").toString().isEmpty()) {
                String sfsh = params.get("sfsh").toString();
                ew.eq("sfsh", sfsh);
                log.debug("审核状态过滤：{}", sfsh);
            }
            
            // 4. 执行分页查询
            PageUtils page = jingsaibaomingService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(ew, params), params)
            );
            
            // 4. 为每条报名记录填充竞赛类型
            if (page != null && page.getList() != null) {
                for (Object obj : page.getList()) {
                    JingsaibaomingEntity baoming = (JingsaibaomingEntity) obj;
                    
                    // 根据竞赛ID查询竞赛类型
                    if (baoming.getJingsaiId() != null) {
                        JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(baoming.getJingsaiId());
                        
                        if (jingsai != null && jingsai.getJingsaileixing() != null) {
                            baoming.setJingsaileixing(jingsai.getJingsaileixing());
                        }
                    }
                }
            }
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("报名分页查询异常：", e);
            System.err.println("报名分页查询异常：" + e.getMessage());
            e.printStackTrace();
            return R.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 前端分页列表查询
     * 功能：对外公开的报名信息查询 (一般对学生开放)
     * 
     * @param params 查询参数 (分页、排序等)
     * @param jingsaibaoming 报名信息实体 (用于条件查询)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, 
                  JingsaibaomingEntity jingsaibaoming,
                  HttpServletRequest request) {
        try {
            // 1. 权限控制：根据用户角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            
            // 如果前端传递了gonghao参数，则进行过滤（用于"我的报名"页面）
            if (params.get("gonghao") != null && !params.get("gonghao").toString().isEmpty()) {
                String gonghao = params.get("gonghao").toString();
                            
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                            
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    // 提取竞赛 ID 列表
                    List<Long> myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(Collectors.toList());
                    // 使用 IN 查询，只查询这些竞赛的报名
                    params.put("jingsai_id_in", myJingsaiIds);
                    log.info("教师 {} 查询自己组织的 {} 个竞赛的报名列表", gonghao, myJingsaiIds.size());
                } else {
                    // 该教师没有创建任何竞赛，返回空结果
                    params.put("jingsai_id", -1);
                    log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                }
            } else if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的报名
                String xuehao = (String) request.getSession().getAttribute("username");
                if (xuehao != null && !xuehao.isEmpty()) {
                    params.put("xuehao", xuehao);
                    log.info("学生 {} 查询自己的报名列表", xuehao);
                }
            } else if ("jiaoshi".equals(tableName)) {
                // 教师只能查看自己组织的竞赛的报名 - 强制过滤
                String gonghao = (String) request.getSession().getAttribute("username");
                if (gonghao != null && !gonghao.isEmpty()) {
                    // 查询该教师创建的所有竞赛ID
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.eq("gonghao", gonghao);
                    List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                    
                    if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                        List<Long> myJingsaiIds = myJingsaiList.stream()
                                .map(JingsaixinxiEntity::getId)
                                .collect(Collectors.toList());
                        params.put("jingsai_id_in", myJingsaiIds);
                        log.info("教师 {} 查询自己组织的 {} 个竞赛的报名列表", gonghao, myJingsaiIds.size());
                    } else {
                        params.put("jingsai_id", -1);
                        log.info("教师 {} 没有创建任何竞赛，返回空列表", gonghao);
                    }
                }
            }
            // 管理员能看到全部数据，不进行权限过滤
            
            // 2. 构建查询条件
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            
            // 处理jingsai_id=-1的情况（教师没有创建任何竞赛，返回空列表）
            if (params.containsKey("jingsai_id") && params.get("jingsai_id") != null) {
                Object jingsaiId = params.get("jingsai_id");
                if (jingsaiId instanceof Integer && (Integer)jingsaiId == -1) {
                    ew.eq("id", -1);  // 设置不可能的条件，返回空列表
                    log.debug("教师没有创建任何竞赛，返回空列表");
                }
            }
            
            // 处理jingsai_id_in参数（用于教师查看自己竞赛的报名）
            if (params.containsKey("jingsai_id_in") && params.get("jingsai_id_in") != null) {
                @SuppressWarnings("unchecked")
                java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
                if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                    ew.in("jingsai_id", jingsaiIds);
                    log.debug("按竞赛ID列表过滤：{}", jingsaiIds);
                }
            }
            
            // 3. 执行分页查询
            PageUtils page = jingsaibaomingService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jingsaibaoming), params), params)
            );
            
            // 4. 为每条报名记录填充竞赛类型
            if (page != null && page.getList() != null) {
                for (Object obj : page.getList()) {
                    JingsaibaomingEntity baoming = (JingsaibaomingEntity) obj;
                    
                    // 根据竞赛ID查询竞赛类型
                    if (baoming.getJingsaiId() != null) {
                        JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(baoming.getJingsaiId());
                        
                        if (jingsai != null && jingsai.getJingsaileixing() != null) {
                            baoming.setJingsaileixing(jingsai.getJingsaileixing());
                        }
                    }
                }
            }
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("报名前端列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 简单列表查询 (不分页)
     * 功能：获取所有符合条件的报名信息
     * 
     * @param jingsaibaoming 报名信息实体 (用于条件查询)
     * @return R 统一返回结果，包含列表数据
     */
    @RequestMapping("/lists")
    public R list(JingsaibaomingEntity jingsaibaoming) {
        try {
            // 1. 构建查询条件 (精确匹配)
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaibaoming, "jingsaibaoming"));
            
            // 2. 查询列表
            return R.ok().put("data", jingsaibaomingService.selectListView(ew));
            
        } catch (Exception e) {
            log.error("报名列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 查询单个报名信息
     * 功能：根据条件查询报名详情
     * 
     * @param jingsaibaoming 报名信息实体 (用于条件查询)
     * @return R 统一返回结果，包含报名视图数据
     */
    @RequestMapping("/query")
    public R query(JingsaibaomingEntity jingsaibaoming) {
        try {
            // 1. 构建查询条件
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(jingsaibaoming, "jingsaibaoming"));
            
            // 2. 查询视图数据 (关联查询)
            JingsaibaomingView jingsaibaomingView = jingsaibaomingService.selectView(ew);
            
            if (jingsaibaomingView == null) {
                log.warn("查询报名信息失败：未找到符合条件的记录");
                return R.error("报名信息不存在");
            }
            
            return R.ok("查询竞赛报名成功").put("data", jingsaibaomingView);
            
        } catch (Exception e) {
            log.error("查询报名信息异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 后端详情查询
     * 功能：根据 ID 查询报名详细信息 (后台管理使用)
     * 
     * @param id 报名 ID
     * @return R 统一返回结果，包含报名详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询报名详情失败：ID 非法，ID: {}", id);
                return R.error("报名 ID 非法");
            }
            
            // 2. 查询报名信息
            JingsaibaomingEntity jingsaibaoming = jingsaibaomingService.selectById(id);
            
            if (jingsaibaoming == null) {
                log.warn("查询报名详情失败：ID{}不存在", id);
                return R.error("报名信息不存在");
            }
            
            return R.ok().put("data", jingsaibaoming);
            
        } catch (Exception e) {
            log.error("查询报名详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端详情查询
     * 功能：根据 ID 查询报名详细信息 (前台展示使用)
     * 
     * @param id 报名 ID
     * @return R 统一返回结果，包含报名详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询报名详情失败：ID 非法，ID: {}", id);
                return R.error("报名 ID 非法");
            }
            
            // 2. 查询报名信息
            JingsaibaomingEntity jingsaibaoming = jingsaibaomingService.selectById(id);
            
            if (jingsaibaoming == null) {
                log.warn("查询报名详情失败：ID{}不存在", id);
                return R.error("报名信息不存在");
            }
            
            return R.ok().put("data", jingsaibaoming);
            
        } catch (Exception e) {
            log.error("查询报名详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 保存报名信息 (后台管理)
     * 功能：学生报名竞赛或管理员代报名
     * 
     * 业务逻辑：
     * 1. 验证必填字段 (学号、参赛人员等)
     * 2. 自动填充学号/工号
     * 3. 生成唯一 ID
     * 4. 保存到数据库
     * 
     * @param jingsaibaoming 报名信息实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @OperationLog("添加竞赛报名")
    @RequestMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaibaomingEntity jingsaibaoming, HttpServletRequest request) {
        log.info("收到报名请求：{}", jingsaibaoming);
        
        // 1. 基础参数校验
        if (!StringUtils.hasText(jingsaibaoming.getXuehao())) {
            log.warn("保存报名失败：学号为空");
            return R.error("学号不能为空");
        }
        
        if (!StringUtils.hasText(jingsaibaoming.getCansairenyuan())) {
            log.warn("保存报名失败：参赛人员为空");
            return R.error("参赛人员不能为空");
        }
        
        // 新增：检查竞赛是否已结束（使用竞赛名称查询）
        if (StringUtils.hasText(jingsaibaoming.getJingsaimingcheng())) {
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            ew.eq("jingsaimingcheng", jingsaibaoming.getJingsaimingcheng());
            JingsaixinxiEntity jingsai = jingsaixinxiService.selectOne(ew);
            
            if (jingsai == null) {
                log.warn("报名失败：竞赛 {} 不存在", jingsaibaoming.getJingsaimingcheng());
                return R.error("该竞赛不存在，无法报名");
            }
            
            if (jingsai.getJingsaishijian() != null) {
                // 如果当前时间超过竞赛时间，则不允许报名
                if (jingsai.getJingsaishijian().before(new Date())) {
                    log.warn("报名失败：竞赛 {} 已结束", jingsai.getJingsaimingcheng());
                    return R.error("该竞赛已结束，无法提交申请");
                }
            }
            
            // 检查竞赛是否允许报名
            log.debug("竞赛 {} 允许报名", jingsai.getJingsaimingcheng());
        }
        
        try {
            // 2. 自动填充学号 (如果是学生自己报名)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                if (StringUtils.hasText(xuehao)) {
                    jingsaibaoming.setXuehao(xuehao);
                    log.info("学生 {} 报名竞赛：{}", xuehao, jingsaibaoming.getJingsaimingcheng());
                } else {
                    log.error("学生 session 中学号为空");
                    return R.error("请先登录");
                }
            }

            // 2.5 兜底处理：如果学生姓名为空，从学生表查询填充
            if (!StringUtils.hasText(jingsaibaoming.getXueshengxingming()) && StringUtils.hasText(jingsaibaoming.getXuehao())) {
                log.warn("报名-学生姓名为空，从学生表查询 - 学号: {}", jingsaibaoming.getXuehao());
                EntityWrapper<com.entity.XueshengEntity> xueshengEw = new EntityWrapper<>();
                xueshengEw.eq("xuehao", jingsaibaoming.getXuehao());
                com.entity.XueshengEntity xuesheng = xueshengService.selectOne(xueshengEw);
                if (xuesheng != null && StringUtils.hasText(xuesheng.getXueshengxingming())) {
                    jingsaibaoming.setXueshengxingming(xuesheng.getXueshengxingming());
                    log.info("报名-从学生表查询到姓名: {}", jingsaibaoming.getXueshengxingming());
                }
            }

            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaibaoming);
            
            // 4. 检查是否重复报名
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            ew.eq("xuehao", jingsaibaoming.getXuehao());
            ew.eq("jingsaimingcheng", jingsaibaoming.getJingsaimingcheng());
            Integer count = jingsaibaomingService.selectCount(ew);
            if (count > 0) {
                log.warn("报名失败：学生 {} 已报名竞赛 {}", 
                        jingsaibaoming.getXuehao(), 
                        jingsaibaoming.getJingsaimingcheng());
                return R.error("您已经报名过该竞赛，不能重复报名");
            }
            
            // 5. 生成唯一 ID 并保存
            jingsaibaoming.setId(IdWorker.getId());
            jingsaibaomingService.insert(jingsaibaoming);
                        
            // 【优化】报名成功后不再生成缴费记录，改为在教师审核通过时生成
            log.info("保存报名信息成功，ID: {}, 学号：{}, 竞赛：{}，等待教师审核", 
                     jingsaibaoming.getId(), 
                     jingsaibaoming.getXuehao(), 
                     jingsaibaoming.getJingsaimingcheng());
            
            // 6. 发送报名通知给教师
            try {
                // 查询竞赛信息，获取负责教师
                if (StringUtils.hasText(jingsaibaoming.getJingsaimingcheng())) {
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.eq("jingsaimingcheng", jingsaibaoming.getJingsaimingcheng());
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectOne(jingsaiEw);
                    
                    if (jingsai != null && StringUtils.hasText(jingsai.getGonghao())) {
                        String teacherGonghao = jingsai.getGonghao();
                        String teacherName = jingsai.getJiaoshixingming();
                        String studentName = jingsaibaoming.getXueshengxingming() != null ? jingsaibaoming.getXueshengxingming() : "学生";
                        
                        String title = "新的报名申请";
                        String content = String.format("学生「%s」提交了「%s」竞赛的报名申请，请及时审核。", studentName, jingsaibaoming.getJingsaimingcheng());
                        
                        xiaoxiTongzhiService.sendTongzhi(
                            title,
                            content,
                            "报名申请",
                            "系统",
                            null,
                            teacherGonghao,
                            "jiaoshi",
                            jingsaibaoming.getId(),
                            "baoming"
                        );
                        log.info("✓ 发送报名通知给教师: {}, 竞赛: {}, 报名ID: {}", teacherGonghao, jingsaibaoming.getJingsaimingcheng(), jingsaibaoming.getId());
                    }
                }
            } catch (Exception e) {
                log.error("发送报名通知异常", e);
                // 通知发送失败不影响报名流程
            }
            
            return R.ok("报名成功，请等待教师审核");
            
        } catch (Exception e) {
            log.error("保存报名信息异常：学号={}, 竞赛={}", 
                     jingsaibaoming.getXuehao(), 
                     jingsaibaoming.getJingsaimingcheng(), e);
            return R.error("报名失败，请联系管理员");
        }
    }

    /**
     * 保存报名信息 (前端)
     * 功能：在前台进行竞赛报名
     * 
     * @param jingsaibaoming 报名信息实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @OperationLog("学生在线报名")
    @RequestMapping("/add")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R add(@RequestBody JingsaibaomingEntity jingsaibaoming, HttpServletRequest request) {
        // 1. 基础参数校验
        if (!StringUtils.hasText(jingsaibaoming.getXuehao())) {
            log.warn("添加报名失败：学号为空");
            return R.error("学号不能为空");
        }
        
        if (!StringUtils.hasText(jingsaibaoming.getCansairenyuan())) {
            log.warn("添加报名失败：参赛人员为空");
            return R.error("参赛人员不能为空");
        }
        
        // 【论文3.1.1(3)】报名截止时间检查 - 竞赛已结束不允许新报名
        if (jingsaibaoming.getJingsaiId() != null) {
            JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(jingsaibaoming.getJingsaiId());
            if (jingsai != null && jingsai.getJingsaishijian() != null && jingsai.getJingsaishijian().before(new Date())) {
                log.warn("报名截止：竞赛{}已结束，不允许报名", jingsai.getJingsaimingcheng());
                return R.error("该竞赛已结束（截止时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(jingsai.getJingsaishijian()) + "），无法提交报名");
            }
        }
        
        try {
            // 2. 自动填充学号 (如果是学生自己报名)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                jingsaibaoming.setXuehao(xuehao);
                log.info("学生 {} 添加竞赛报名：{}", xuehao, jingsaibaoming.getJingsaimingcheng());
            }

            // 2.5 兜底处理：如果学生姓名为空，从学生表查询填充
            if (!StringUtils.hasText(jingsaibaoming.getXueshengxingming()) && StringUtils.hasText(jingsaibaoming.getXuehao())) {
                log.warn("在线报名-学生姓名为空，从学生表查询 - 学号: {}", jingsaibaoming.getXuehao());
                EntityWrapper<com.entity.XueshengEntity> xueshengEw = new EntityWrapper<>();
                xueshengEw.eq("xuehao", jingsaibaoming.getXuehao());
                com.entity.XueshengEntity xuesheng = xueshengService.selectOne(xueshengEw);
                if (xuesheng != null && StringUtils.hasText(xuesheng.getXueshengxingming())) {
                    jingsaibaoming.setXueshengxingming(xuesheng.getXueshengxingming());
                    log.info("在线报名-从学生表查询到姓名: {}", jingsaibaoming.getXueshengxingming());
                }
            }

            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaibaoming);
            
            // 3.5 【关键修复】兜底处理：设置addtime（如果前端未传递或格式错误）
            EntityUtil.setAddtimeIfNull(jingsaibaoming);
            
            // 4. 生成唯一 ID 并保存
            jingsaibaoming.setId(IdWorker.getId());
            jingsaibaomingService.insert(jingsaibaoming);
                        
            // 【优化】报名成功后不再生成缴费记录，改为在教师审核通过时生成
            log.info("添加报名信息成功，ID: {}, 学号：{}, 竞赛：{}，等待教师审核", 
                     jingsaibaoming.getId(), 
                     jingsaibaoming.getXuehao(), 
                     jingsaibaoming.getJingsaimingcheng());
            
            // 5. 发送报名通知给教师
            try {
                // 查询竞赛信息，获取负责教师
                if (StringUtils.hasText(jingsaibaoming.getJingsaimingcheng())) {
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.eq("jingsaimingcheng", jingsaibaoming.getJingsaimingcheng());
                    JingsaixinxiEntity jingsai = jingsaixinxiService.selectOne(jingsaiEw);
                    
                    if (jingsai != null && StringUtils.hasText(jingsai.getGonghao())) {
                        String teacherGonghao = jingsai.getGonghao();
                        String teacherName = jingsai.getJiaoshixingming();
                        String studentName = jingsaibaoming.getXueshengxingming() != null ? jingsaibaoming.getXueshengxingming() : "学生";
                        
                        String title = "新的报名申请";
                        String content = String.format("学生「%s」提交了「%s」竞赛的报名申请，请及时审核。", studentName, jingsaibaoming.getJingsaimingcheng());
                        
                        xiaoxiTongzhiService.sendTongzhi(
                            title,
                            content,
                            "报名申请",
                            "系统",
                            null,
                            teacherGonghao,
                            "jiaoshi",
                            jingsaibaoming.getId(),
                            "baoming"
                        );
                        log.info("✓ 发送报名通知给教师: {}, 竞赛: {}, 报名ID: {}", teacherGonghao, jingsaibaoming.getJingsaimingcheng(), jingsaibaoming.getId());
                    }
                }
            } catch (Exception e) {
                log.error("发送报名通知异常", e);
                // 通知发送失败不影响报名流程
            }
            
            return R.ok("报名成功，请等待教师审核");
            
        } catch (Exception e) {
            log.error("添加报名信息异常：", e);
            return R.error("报名失败，请联系管理员");
        }
    }

    /**
     * 修改报名信息
     * 功能：更新已有的报名信息
     * 
     * 【重要】当审核状态从"否"变为"是"时，会自动生成缴费记录
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 验证实体完整性
     * 3. 检查是否为审核操作（sfsh字段变更）
     * 4. 如果是首次审核通过，生成缴费记录
     * 5. 更新到数据库
     * 
     * @param jingsaibaoming 报名实体
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @OperationLog("修改竞赛报名信息")
    @RequestMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaibaomingEntity jingsaibaoming, HttpServletRequest request) {
        // 1. 参数校验
        if (jingsaibaoming.getId() == null || jingsaibaoming.getId() <= 0) {
            log.warn("修改报名失败：ID 非法，ID: {}", jingsaibaoming.getId());
            return R.error("报名 ID 非法");
        }
        
        if (!StringUtils.hasText(jingsaibaoming.getXuehao())) {
            log.warn("修改报名失败：学号为空，ID: {}", jingsaibaoming.getId());
            return R.error("学号不能为空");
        }
        
        try {
            // 2. 【论文3.1.1(3)】报名截止时间自动锁定 - 检查竞赛是否已结束
            JingsaibaomingEntity oldBaomingForDeadline = jingsaibaomingService.selectById(jingsaibaoming.getId());
            if (oldBaomingForDeadline != null && oldBaomingForDeadline.getJingsaiId() != null) {
                JingsaixinxiEntity jingsai = jingsaixinxiService.selectById(oldBaomingForDeadline.getJingsaiId());
                if (jingsai != null && jingsai.getJingsaishijian() != null) {
                    // 如果当前时间超过竞赛时间，则不允许修改报名信息
                    if (jingsai.getJingsaishijian().before(new Date())) {
                        // 只有审核操作（教师审核通过/驳回）才允许在截止后执行
                        String newSfsh = jingsaibaoming.getSfsh();
                        boolean isShenheOnly = "通过".equals(newSfsh) || "未通过".equals(newSfsh);
                        if (!isShenheOnly) {
                            log.warn("报名截止锁定：竞赛{}已结束，不允许修改报名，ID：{}", jingsai.getJingsaimingcheng(), jingsaibaoming.getId());
                            return R.error("该竞赛已结束（截止时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(jingsai.getJingsaishijian()) + "），报名信息已锁定，不允许修改");
                        }
                    }
                }
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaibaoming);
            
            // 4. 【核心】检查是否为审核操作（sfsh字段变更）
            String newSfsh = jingsaibaoming.getSfsh();
            boolean isShenheAction = "通过".equals(newSfsh);
            boolean isBohuiAction = "未通过".equals(newSfsh);
            
            if (isShenheAction || isBohuiAction) {
                // 查询原报名记录，判断是否从未审核状态变为通过/驳回
                JingsaibaomingEntity oldBaoming = jingsaibaomingService.selectById(jingsaibaoming.getId());
                if (oldBaoming != null) {
                    String oldSfsh = oldBaoming.getSfsh();
                    // 【幂等保护】如果已审核通过或已驳回，不允许重复审核
                    if ("通过".equals(oldSfsh) || "未通过".equals(oldSfsh)) {
                        log.warn("报名已审核，当前状态：{}，ID：{}，请勿重复操作", oldSfsh, jingsaibaoming.getId());
                        return R.error("该报名已审核，当前状态为【" + oldSfsh + "】，请勿重复操作");
                    }
                    boolean wasNotApproved = !"通过".equals(oldSfsh);
                    
                    // 如果是首次审核通过，生成缴费记录
                    if (isShenheAction && wasNotApproved) {
                        log.info("报名ID: {} 审核通过，准备生成缴费记录", jingsaibaoming.getId());
                        generateJiaofeiJiluOnApproval(jingsaibaoming);
                        
                        // 发送审核通过消息给学生
                        String studentXuehao = oldBaoming.getXuehao();
                        String studentName = oldBaoming.getXueshengxingming();
                        String competitionName = oldBaoming.getJingsaimingcheng();
                        String teacherName = (String) request.getSession().getAttribute("xingming");
                        
                        String title = "报名审核通过";
                        String content = String.format("您的「%s」竞赛报名已通过审核。", competitionName);
                        if (teacherName != null && !teacherName.isEmpty()) {
                            content += "审核教师：" + teacherName;
                        }
                        
                        xiaoxiTongzhiService.sendTongzhi(
                            title,
                            content,
                            "审核结果",
                            teacherName != null ? teacherName : "系统",
                            studentXuehao,
                            null,
                            "xuesheng",
                            oldBaoming.getId(),
                            "baoming"
                        );
                        log.info("✓ 发送审核通过消息给学生: {}, 报名ID: {}", studentXuehao, oldBaoming.getId());
                    }
                    
                    // 如果是审核驳回，发送消息给学生
                    if (isBohuiAction && wasNotApproved) {
                        String studentXuehao = oldBaoming.getXuehao();
                        String studentName = oldBaoming.getXueshengxingming();
                        String competitionName = oldBaoming.getJingsaimingcheng();
                        String teacherName = (String) request.getSession().getAttribute("xingming");
                        
                        String title = "报名审核未通过";
                        String content = String.format("您的「%s」竞赛报名审核未通过，请联系教师了解详情。", competitionName);
                        
                        xiaoxiTongzhiService.sendTongzhi(
                            title,
                            content,
                            "审核结果",
                            teacherName != null ? teacherName : "系统",
                            studentXuehao,
                            null,
                            "xuesheng",
                            oldBaoming.getId(),
                            "baoming"
                        );
                        log.info("✓ 发送审核驳回消息给学生: {}, 报名ID: {}", studentXuehao, oldBaoming.getId());
                    }
                }
            }
            
            // 4. 执行更新
            jingsaibaomingService.updateById(jingsaibaoming);
            
            log.info("修改报名信息成功，ID: {}, 学号：{}, 审核状态: {}", 
                     jingsaibaoming.getId(), jingsaibaoming.getXuehao(), jingsaibaoming.getSfsh());
            return R.ok("修改成功");
            
        } catch (Exception e) {
            log.error("修改报名信息 ID{}异常：", jingsaibaoming.getId(), e);
            return R.error("修改失败，请联系管理员");
        }
    }

    /**
     * 删除报名信息
     * 功能：批量删除报名记录
     * 
     * 注意：此操作不可逆，请谨慎使用
     * 
     * @param ids 报名 ID 数组
     * @return R 统一返回结果
     */
    @OperationLog("删除竞赛报名信息")
    @RequestMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody String[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除报名失败：ID 数组为空");
            return R.error("请选择要删除的报名");
        }
        
        try {
            // 将 String[] 转换为 Long[]，避免前端传递大数字时的精度丢失问题
            Long[] longIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                try {
                    longIds[i] = Long.parseLong(ids[i]);
                } catch (NumberFormatException e) {
                    log.error("ID 格式错误: {}", ids[i]);
                    return R.error("报名 ID 格式错误: " + ids[i]);
                }
            }
            
            log.info("接收到的删除请求，原始 IDs: {}，转换后 Long IDs: {}", Arrays.toString(ids), Arrays.toString(longIds));
            
            // 2. 批量删除
            jingsaibaomingService.deleteBatchIds(Arrays.asList(longIds));
            
            log.info("删除报名信息成功，IDs: {}", Arrays.toString(longIds));
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除报名 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
        }
    }

    /**
     * 【核心】报名审核通过时生成缴费记录
     * 功能：当教师审核通过报名时，根据竞赛费用配置自动生成缴费记录
     * 
     * 逻辑：
     * - 如果竞赛免费（shifou_shoufei=否）：直接设置缴费状态为"已通过"，金额0.00
     * - 如果竞赛收费（shifou_shoufei=是）：设置缴费状态为"未缴费"，等待学生缴费
     * 
     * @param baoming 报名实体
     */
    private void generateJiaofeiJiluOnApproval(JingsaibaomingEntity baoming) {
        // 定义常量
        final String STATUS_APPROVED = "已通过";
        final String STATUS_UNPAID = "未缴费";
        final String MSG_AUTO_APPROVE_FREE = "免费竞赛，系统自动通过";
            
        try {
            log.info("开始生成缴费记录，报名ID: {}, 学号: {}, 竞赛: {}", 
                    baoming.getId(), baoming.getXuehao(), baoming.getJingsaimingcheng());
                
            // 1. 查询竞赛信息（优先使用jingsaiId）
            JingsaixinxiEntity jingsai = null;
            if (baoming.getJingsaiId() != null && baoming.getJingsaiId() > 0) {
                jingsai = jingsaixinxiService.selectById(baoming.getJingsaiId());
            }
                
            // 如果jingsaiId为空或查询失败，尝试通过竞赛名称查询
            if (jingsai == null && StringUtils.hasText(baoming.getJingsaimingcheng())) {
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("jingsaimingcheng", baoming.getJingsaimingcheng());
                jingsai = jingsaixinxiService.selectOne(jingsaiEw);
            }
                
            if (jingsai == null) {
                log.error("生成缴费记录失败：竞赛不存在，报名ID: {}", baoming.getId());
                throw new RuntimeException("竞赛信息不存在，无法生成缴费记录");
            }
                
            // 2. 检查是否已存在缴费记录（防止重复生成）
            EntityWrapper<com.entity.JingsaiJiaofeiJiluEntity> checkEw = new EntityWrapper<>();
            checkEw.eq("baoming_id", baoming.getId());
            Integer existCount = jiaofeiJiluService.selectCount(checkEw);
            if (existCount != null && existCount > 0) {
                log.warn("报名ID: {} 已存在缴费记录，跳过生成", baoming.getId());
                return;
            }
                
            // 3. 【核心】根据竞赛的缴费模式(jiaofeimoshi)决定缴费流程
            // jiaofeimoshi='免费' -> 不需要缴费，自动通过
            // jiaofeimoshi='付费' -> 需要缴费，等待学生提交凭证
            String jiaofeimoshi = jingsai.getJiaofeimoshi();
            boolean isFreeCompetition = "免费".equals(jiaofeimoshi);
                
            // 4. 创建缴费记录
            com.entity.JingsaiJiaofeiJiluEntity jiaofeiJilu = new com.entity.JingsaiJiaofeiJiluEntity();
            jiaofeiJilu.setId(IdWorker.getId());
            jiaofeiJilu.setBaomingId(baoming.getId());
            jiaofeiJilu.setXuehao(baoming.getXuehao());
            jiaofeiJilu.setXueshengxingming(baoming.getXueshengxingming());
            jiaofeiJilu.setJingsaiId(jingsai.getId());
            jiaofeiJilu.setJingsaimingcheng(baoming.getJingsaimingcheng());
            jiaofeiJilu.setJiaofeiShijian(new Date());
            jiaofeiJilu.setAddtime(new Date());
                
            if (isFreeCompetition) {
                // 免费竞赛：直接设置为"已通过"，金额0.00
                jiaofeiJilu.setJiaofeiJine(new java.math.BigDecimal("0.00"));
                jiaofeiJilu.setJiaofeiZhuangtai(STATUS_APPROVED);
                jiaofeiJilu.setShenheYijian(MSG_AUTO_APPROVE_FREE);
                jiaofeiJilu.setShenheShijian(new Date());
                    
                log.info("✓ 免费竞赛，报名ID: {}, 自动通过缴费", baoming.getId());
            } else {
                // 付费竞赛：设置为"未缴费"，等待学生提交缴费凭证
                // 从竞赛信息中直接获取费用金额
                java.math.BigDecimal feeAmount = jingsai.getJingsaiFeiyong();
                if (feeAmount == null || feeAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    log.warn("付费竞赛费用金额为空或0，报名ID: {}, 竞赛ID: {}", baoming.getId(), jingsai.getId());
                    feeAmount = java.math.BigDecimal.ZERO;
                }
                jiaofeiJilu.setJiaofeiJine(feeAmount);
                jiaofeiJilu.setJiaofeiZhuangtai(STATUS_UNPAID);
                    
                log.info("✓ 付费竞赛，报名ID: {}, 金额: {}, 等待学生缴费", baoming.getId(), feeAmount);
            }
                
            // 5. 保存缴费记录
            jiaofeiJiluService.insert(jiaofeiJilu);
            log.info("✓ 缴费记录生成成功，报名ID: {}, 状态: {}", baoming.getId(), jiaofeiJilu.getJiaofeiZhuangtai());
                
            // 6. 如果是免费竞赛，同步更新报名表的支付状态
            if (isFreeCompetition) {
                baoming.setIspay("已缴费");
                jingsaibaomingService.updateById(baoming);
                log.info("✓ 免费竞赛，报名表支付状态已更新，报名ID: {}, ispay: 已缴费", baoming.getId());
            }
                
        } catch (RuntimeException e) {
            // 业务异常，直接抛出
            log.error("✗ 生成缴费记录业务异常，报名ID: {}, 原因: {}", baoming.getId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            // 系统异常，包装后抛出
            log.error("✗ 生成缴费记录系统异常，报名ID: {}", baoming.getId(), e);
            throw new RuntimeException("生成缴费记录失败：" + e.getMessage(), e);
        }
    }

    /**
     * 提醒功能接口
     * 功能：统计待审核或其他状态的报名数量
     * 
     * 应用场景：
     * - 教师端：显示待审核的报名数量
     * - 学生端：显示我报名的竞赛提醒
     * 
     * @param columnName 字段名 (如 baomingshijian-报名时间)
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
            Wrapper<JingsaibaomingEntity> wrapper = new EntityWrapper<>();
            if (map.get("remindstart") != null) {
                wrapper.ge(columnName, map.get("remindstart")); // 大于等于开始时间
            }
            if (map.get("remindend") != null) {
                wrapper.le(columnName, map.get("remindend")); // 小于等于结束时间
            }
            
            // 4. 权限控制：根据用户角色过滤
            String tableName = (String) request.getSession().getAttribute("tableName");
            if (tableName != null) {
                if ("jiaoshi".equals(tableName)) {
                    String gonghao = (String) request.getSession().getAttribute("username");
                    wrapper.eq("gonghao", gonghao);
                    log.debug("教师 {} 查询报名提醒", gonghao);
                } else if ("xuesheng".equals(tableName)) {
                    String xuehao = (String) request.getSession().getAttribute("username");
                    wrapper.eq("xuehao", xuehao);
                    log.debug("学生 {} 查询报名提醒", xuehao);
                }
            }
            
            // 5. 统计数量
            int count = jingsaibaomingService.selectCount(wrapper);
            
            log.debug("报名提醒查询成功，字段：{}, 类型：{}, 数量：{}", columnName, type, count);
            return R.ok().put("count", count);
            
        } catch (Exception e) {
            log.error("报名提醒查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 获取统计数据
     * 功能：统计报名总数、已通过数、待审核数
     * 教师只能统计自己创建的竞赛的报名数据
     * 
     * @param params 查询参数（支持gonghao参数）
     * @param request HTTP 请求
     * @return R 统一返回结果，包含统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        log.info("=========== 报名统计查询开始 ===========");
        log.info("请求参数：{}", params);
        try {
            // 根据用户角色和前端参数设置查询参数
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.info("当前用户角色tableName：{}", tableName);
            
            // 优先检查前端传递的gonghao参数（用于my-list.vue页面，教师只看自己竞赛的报名）
            if (params.get("gonghao") != null && !params.get("gonghao").toString().isEmpty()) {
                String gonghao = params.get("gonghao").toString();
                log.info("前端传递的工号：{}", gonghao);
                
                // 查询该教师创建的所有竞赛ID
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    // 提取竞赛 ID 列表
                    List<Long> myJingsaiIds = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getId)
                            .collect(Collectors.toList());
                    params.put("jingsai_id_in", myJingsaiIds);
                    log.info("教师 {} 统计自己组织的 {} 个竞赛的报名数据", gonghao, myJingsaiIds.size());
                } else {
                    // 该教师没有创建任何竞赛，返回空统计
                    params.put("jingsai_id", -1);
                    log.info("教师 {} 没有创建任何竞赛，返回空统计", gonghao);
                }
            } else if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的数据
                String xuehao = (String) request.getSession().getAttribute("username");
                log.info("学生学号：{}", xuehao);
                if (xuehao != null) {
                    params.put("xuehao", xuehao);
                }
            } else if ("jiaoshi".equals(tableName)) {
                // 教师可以查看所有竞赛的报名统计（报名管理页面需要显示所有竞赛的报名）
                // 如果前端传递了gonghao参数，则按工号过滤（用于"我的报名"等页面）
                log.info("教师用户查看报名统计，不做自动过滤，显示所有竞赛的报名统计");
            }
            // 管理员能看到全部数据，不进行权限过滤
            
            log.info("最终查询参数：{}", params);
            Map<String, Object> statistics = jingsaibaomingService.getStatistics(params);
            log.info("统计结果：{}", statistics);
            return R.ok().put("data", statistics);
            
        } catch (Exception e) {
            log.error("获取报名统计数据异常：", e);
            return R.error("查询失败，请重试");
        }
    }
}
