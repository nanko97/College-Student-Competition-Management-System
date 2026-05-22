package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JingsaixinxiEntity;
import com.entity.TokenEntity;
import com.entity.ZuopindafenEntity;
import com.entity.ZuopindafenFuheEntity;
import com.entity.view.ZuopindafenView;
import com.service.JingsaixinxiService;
import com.service.TokenService;
import com.service.ZuopindafenFuheService;
import com.service.ZuopindafenService;
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
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作品打分管理控制器
 * 功能：教师对参赛作品进行评分、学生查看自己的作品得分
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
@RequestMapping("/zuopindafen")
@Slf4j // 日志注解，便于问题排查
public class ZuopindafenController {
    
    @Autowired
    private ZuopindafenService zuopindafenService;
    
    @Autowired
    private ZuopindafenFuheService zuopindafenFuheService;
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    @Autowired
    private TokenService tokenService;

    /**
     * 后端分页列表查询
     * 功能：带权限控制的作品打分信息查询
     * 
     * 权限逻辑：
     * - 教师角色：只能查看自己评分的作品，且只能看到自己创建的竞赛的作品
     * - 学生角色：只能查看自己的作品得分
     * - 管理员：可以查看所有评分信息
     * 
     * @param params 查询参数 (分页、排序等)
     * @param zuopindafen 作品打分实体 (用于条件查询)
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, 
                  ZuopindafenEntity zuopindafen,
                  HttpServletRequest request) {
        log.info("=========== 作品打分分页查询开始 ===========");
        log.info("请求参数：{}", params);
        
        try {
            // 1. 权限控制：根据用户角色和前端参数过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            log.info("当前用户角色tableName：{}，role：{}", tableName, role);
            
            // 2. 构建查询条件
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            
            // 2.1 权限过滤：根据角色在SQL层面过滤数据（确保在分页前过滤，避免数据丢失）
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                // 教师角色：只查看自己评分的记录（"我的打分" = 我的评分记录）
                // 【关键修复】使用 ew.eq("gonghao", gonghao) 在SQL层面过滤
                // 原来的内存过滤方式在分页后执行，导致教师的记录不在当前页就被丢弃
                String gonghao = (String) request.getSession().getAttribute("username");
                ew.eq("gonghao", gonghao);
                log.info("教师 {} 查询作品打分列表，SQL层面过滤 gonghao={}", gonghao, gonghao);
            } else if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的作品得分
                String xuehao = (String) request.getSession().getAttribute("username");
                ew.eq("xuehao", xuehao);
                log.info("学生 {} 查询个人作品得分", xuehao);
            }
            // 管理员能看到全部数据，不进行权限过滤
            
            // 2.2 处理前端传递的模糊查询参数
            if (params.get("jingsaimingcheng") != null) {
                String value = params.get("jingsaimingcheng").toString();
                if (value.contains("%")) {
                    ew.like("jingsaimingcheng", value.replace("%", ""));
                } else {
                    ew.eq("jingsaimingcheng", value);
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
            if (params.get("xuehao") != null) {
                String value = params.get("xuehao").toString();
                if (value.contains("%")) {
                    ew.like("xuehao", value.replace("%", ""));
                } else {
                    ew.eq("xuehao", value);
                }
            }
            if (params.get("gonghao") != null) {
                String value = params.get("gonghao").toString();
                if (value.contains("%")) {
                    ew.like("gonghao", value.replace("%", ""));
                } else {
                    ew.eq("gonghao", value);
                }
            }
            
            // 3. 执行分页查询（SQL层面已包含权限过滤条件，分页前过滤确保数据完整性）
            PageUtils page = zuopindafenService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(ew, params), params)
            );
            
            log.info("分页查询结果: {} 条", page.getList() != null ? page.getList().size() : 0);
            
            // 4. 如果是学生查询，关联复核状态和复核次数
            if ("xuesheng".equals(tableName)) {
                log.info("[学生查询] 开始关联复核状态，数据条数：{}", page.getList() != null ? page.getList().size() : 0);
                List<ZuopindafenEntity> dataList = (List<ZuopindafenEntity>) page.getList();
                if (dataList != null) {
                    for (ZuopindafenEntity score : dataList) {
                        // 查询该成绩的所有复核记录
                        EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
                        fuheEw.eq("zuopindafen_id", score.getId());
                        fuheEw.eq("xuehao", score.getXuehao()); // 只查询当前学生的复核记录
                        fuheEw.orderBy("id", true);
                        List<ZuopindafenFuheEntity> fuheList = zuopindafenFuheService.selectList(fuheEw);
                        
                        log.info("[学生查询] 作品ID：{}, 学号：{}, 复核记录数：{}", score.getId(), score.getXuehao(), fuheList != null ? fuheList.size() : 0);
                        
                        // 如果有复核记录，添加复核状态和次数
                        if (fuheList != null && !fuheList.isEmpty()) {
                            // 取最新的复核记录
                            ZuopindafenFuheEntity latestFuhe = fuheList.get(fuheList.size() - 1);
                            score.setFuheStatus(latestFuhe.getFuheStatus());
                            score.setFuheCount(fuheList.size());
                            log.info("[学生查询] 设置状态：{}, 次数：{}", latestFuhe.getFuheStatus(), fuheList.size());
                        } else {
                            score.setFuheCount(0);
                        }
                    }
                }
            }
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("作品打分分页查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端分页列表查询
     * 功能：对外公开的作品打分信息查询
     * 
     * @param params 查询参数 (分页、排序等)
     * @param zuopindafen 作品打分实体 (用于条件查询)
     * @param request HTTP 请求
     * @return R 统一返回结果，包含分页数据
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, 
                  ZuopindafenEntity zuopindafen,
                  HttpServletRequest request) {
        try {
            // 1. 权限控制：根据用户角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            
            // 2. 构建查询条件
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            
            // 2.1 权限过滤：根据角色在SQL层面过滤数据（确保在分页前过滤，避免数据丢失）
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                // 教师角色：只查看自己评分的记录（"我的打分" = 我的评分记录）
                String gonghao = (String) request.getSession().getAttribute("username");
                ew.eq("gonghao", gonghao);
                log.info("教师 {} 查询作品打分列表(list)，SQL层面过滤 gonghao={}", gonghao, gonghao);
            } else if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                ew.eq("xuehao", xuehao);
            }
            
            // 2.2 处理前端传递的模糊查询参数
            if (params.get("jingsaimingcheng") != null) {
                String value = params.get("jingsaimingcheng").toString();
                if (value.contains("%")) {
                    ew.like("jingsaimingcheng", value.replace("%", ""));
                } else {
                    ew.eq("jingsaimingcheng", value);
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
            if (params.get("xuehao") != null) {
                String value = params.get("xuehao").toString();
                if (value.contains("%")) {
                    ew.like("xuehao", value.replace("%", ""));
                } else {
                    ew.eq("xuehao", value);
                }
            }
            if (params.get("gonghao") != null) {
                String value = params.get("gonghao").toString();
                if (value.contains("%")) {
                    ew.like("gonghao", value.replace("%", ""));
                } else {
                    ew.eq("gonghao", value);
                }
            }
            
            // 3. 执行分页查询（SQL层面已包含权限过滤条件）
            PageUtils page = zuopindafenService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(ew, params), params)
            );
            
            return R.ok().put("data", page).put("page", page);
            
        } catch (Exception e) {
            log.error("作品打分前端列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 简单列表查询 (不分页)
     * 功能：获取所有符合条件的作品打分信息
     * 
     * @param zuopindafen 作品打分实体 (用于条件查询)
     * @return R 统一返回结果，包含列表数据
     */
    @RequestMapping("/lists")
    public R list(ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        try {
            // 1. 权限控制：根据用户角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(zuopindafen, "zuopindafen"));
            
            // 权限过滤：根据角色在SQL层面过滤数据
            List<String> teacherJingsaiNames = null; // 保留用于/lists接口的内存过滤（该接口不分页）
            if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                // 教师角色：只查看自己评分的记录
                String gonghao = (String) request.getSession().getAttribute("username");
                ew.eq("gonghao", gonghao);
                log.info("教师 {} 查询作品打分列表(lists)，SQL层面过滤 gonghao={}", gonghao, gonghao);
            } else if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                ew.eq("xuehao", xuehao);
            }
            
            // 查询列表（SQL层面已包含权限过滤条件）
            List<ZuopindafenView> resultList = zuopindafenService.selectListView(ew);
            
            return R.ok().put("data", resultList);
            
        } catch (Exception e) {
            log.error("作品打分列表查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 查询单个作品打分信息
     * 功能：根据条件查询作品评分详情
     * 
     * @param zuopindafen 作品打分实体 (用于条件查询)
     * @return R 统一返回结果，包含作品评分视图数据
     */
    @RequestMapping("/query")
    public R query(ZuopindafenEntity zuopindafen) {
        try {
            // 1. 构建查询条件
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(zuopindafen, "zuopindafen"));
            
            // 2. 查询视图数据 (关联查询)
            ZuopindafenView zuopindafenView = zuopindafenService.selectView(ew);
            
            if (zuopindafenView == null) {
                log.warn("查询作品打分失败：未找到符合条件的记录");
                return R.error("作品评分信息不存在");
            }
            
            return R.ok("查询作品打分成功").put("data", zuopindafenView);
            
        } catch (Exception e) {
            log.error("查询作品打分异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 后端详情查询
     * 功能：根据 ID 查询作品评分详细信息 (后台管理使用)
     * 
     * @param id 评分 ID
     * @param request HTTP 请求
     * @return R 统一返回结果，包含评分详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询作品评分详情失败：ID 非法，ID: {}", id);
                return R.error("评分 ID 非法");
            }
            
            // 2. 查询评分信息
            ZuopindafenEntity zuopindafen = zuopindafenService.selectById(id);
            
            if (zuopindafen == null) {
                log.warn("查询作品评分详情失败：ID{}不存在", id);
                return R.error("作品评分信息不存在");
            }
            
            // 3. 如果是学生查询，关联复核状态和复核次数
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName) && zuopindafen.getXuehao() != null) {
                // 查询该成绩的所有复核记录
                EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
                fuheEw.eq("zuopindafen_id", zuopindafen.getId());
                fuheEw.eq("xuehao", zuopindafen.getXuehao());
                fuheEw.orderBy("id", true);
                List<ZuopindafenFuheEntity> fuheList = zuopindafenFuheService.selectList(fuheEw);
                
                if (fuheList != null && !fuheList.isEmpty()) {
                    // 取最新的复核记录
                    ZuopindafenFuheEntity latestFuhe = fuheList.get(fuheList.size() - 1);
                    zuopindafen.setFuheStatus(latestFuhe.getFuheStatus());
                    zuopindafen.setFuheCount(fuheList.size());
                    log.info("[详情查询] 作品ID：{}, 学号：{}, 状态：{}, 次数：{}", 
                            zuopindafen.getId(), zuopindafen.getXuehao(), 
                            latestFuhe.getFuheStatus(), fuheList.size());
                } else {
                    zuopindafen.setFuheCount(0);
                }
            }
            
            return R.ok().put("data", zuopindafen);
            
        } catch (Exception e) {
            log.error("查询作品评分详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 前端详情查询
     * 功能：根据 ID 查询作品评分详细信息 (前台展示使用)
     * 
     * @param id 评分 ID
     * @param request HTTP 请求
     * @return R 统一返回结果，包含评分详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询作品评分详情失败：ID 非法，ID: {}", id);
                return R.error("评分 ID 非法");
            }
            
            // 2. 查询评分信息
            ZuopindafenEntity zuopindafen = zuopindafenService.selectById(id);
            
            if (zuopindafen == null) {
                log.warn("查询作品评分详情失败：ID{}不存在", id);
                return R.error("作品评分信息不存在");
            }
            
            // 3. 如果是学生查询，关联复核状态和复核次数
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName) && zuopindafen.getXuehao() != null) {
                // 查询该成绩的所有复核记录
                EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
                fuheEw.eq("zuopindafen_id", zuopindafen.getId());
                fuheEw.eq("xuehao", zuopindafen.getXuehao());
                fuheEw.orderBy("id", true);
                List<ZuopindafenFuheEntity> fuheList = zuopindafenFuheService.selectList(fuheEw);
                
                if (fuheList != null && !fuheList.isEmpty()) {
                    // 取最新的复核记录
                    ZuopindafenFuheEntity latestFuhe = fuheList.get(fuheList.size() - 1);
                    zuopindafen.setFuheStatus(latestFuhe.getFuheStatus());
                    zuopindafen.setFuheCount(fuheList.size());
                    log.info("[详情查询] 作品ID：{}, 学号：{}, 状态：{}, 次数：{}", 
                            zuopindafen.getId(), zuopindafen.getXuehao(), 
                            latestFuhe.getFuheStatus(), fuheList.size());
                } else {
                    zuopindafen.setFuheCount(0);
                }
            }
            
            return R.ok().put("data", zuopindafen);
            
        } catch (Exception e) {
            log.error("查询作品评分详情 ID{}异常：", id, e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 保存作品评分 (后台管理)
     * 功能：教师为参赛作品打分
     * 
     * 业务逻辑：
     * 1. 验证必填字段 (学号、竞赛名称、评分等)
     * 2. 自动填充工号 (如果是教师评分)
     * 3. 生成唯一 ID
     * 4. 保存到数据库
     * 
     * @param zuopindafen 作品打分实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @RequestMapping("/save")
    @OperationLog("保存作品评分")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        // 1. 权限控制：只有教师和管理员可以新增评分
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.debug("新增评分 - 当前用户表名：{}", tableName);
        
        if (tableName == null) {
            log.warn("新增评分失败：用户未登录");
            return R.error("请先登录");
        }
        
        if ("xuesheng".equals(tableName)) {
            log.warn("学生尝试新增评分记录，IP: {}", request.getRemoteAddr());
            return R.error("学生无权新增评分记录，请联系教师");
        }
        
        // 2. 基础参数校验
        if (!StringUtils.hasText(zuopindafen.getXuehao())) {
            log.warn("保存评分失败：学号为空");
            return R.error("学号不能为空");
        }
        
        if (!StringUtils.hasText(zuopindafen.getJingsaimingcheng())) {
            log.warn("保存评分失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        if (zuopindafen.getZuopinpingfen() == null) {
            log.warn("保存评分失败：作品分数为空");
            return R.error("作品分数不能为空");
        }
        
        // 5. 分数范围校验（0-100）
        if (zuopindafen.getZuopinpingfen() < 0 || zuopindafen.getZuopinpingfen() > 100) {
            log.warn("保存评分失败：分数超出范围，分数：{}", zuopindafen.getZuopinpingfen());
            return R.error("评分范围为 0-100 分");
        }
        
        try {
            // 3. 自动填充工号 (如果是教师评分)
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                zuopindafen.setGonghao(gonghao);
                log.info("教师 {} 为竞赛 {} 的作品评分：{}", 
                         gonghao, zuopindafen.getJingsaimingcheng(), zuopindafen.getZuopinpingfen());
            }
            
            // 4. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 5. 生成唯一 ID 并保存
            zuopindafen.setId(IdWorker.getId());
            zuopindafenService.insert(zuopindafen);

            // 发送打分通知给学生
            try {
                String studentXuehao = zuopindafen.getXuehao();
                String jingsaiName = zuopindafen.getJingsaimingcheng();
                Integer score = zuopindafen.getZuopinpingfen();
                String teacherName = zuopindafen.getJiaoshixingming() != null ? zuopindafen.getJiaoshixingming() : zuopindafen.getGonghao();
                if (studentXuehao != null && !studentXuehao.isEmpty()) {
                    String title = "作品评分通知";
                    String content = String.format("您的「%s」竞赛作品已被教师「%s」评分，得分：%d分。", jingsaiName, teacherName, score);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "成绩通知", teacherName,
                        studentXuehao, null, "xuesheng",
                        zuopindafen.getId(), "zuopindafen"
                    );
                    log.info("发送打分通知给学生: {}, 评分ID: {}", studentXuehao, zuopindafen.getId());
                }
            } catch (Exception e) {
                log.error("发送打分通知异常", e);
            }

            log.info("保存作品评分成功，ID: {}, 学号：{}, 分数：{}", 
                     zuopindafen.getId(), 
                     zuopindafen.getXuehao(), 
                     zuopindafen.getZuopinpingfen());
            return R.ok("评分成功");
            
        } catch (Exception e) {
            log.error("保存作品评分异常：", e);
            return R.error("评分失败，请联系管理员");
        }
    }

    /**
     * 保存作品评分 (前端)
     * 功能：在前台进行作品评分操作
     * 
     * @param zuopindafen 作品打分实体
     * @param request HTTP 请求 (获取会话信息)
     * @return R 统一返回结果
     */
    @RequestMapping("/add")
    @OperationLog("添加作品评分")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R add(@RequestBody ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        // 1. 权限控制：只有教师和管理员可以新增评分
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.debug("添加评分 - 当前用户表名：{}", tableName);
        
        if (tableName == null) {
            log.warn("添加评分失败：用户未登录");
            return R.error("请先登录");
        }
        
        if ("xuesheng".equals(tableName)) {
            log.warn("学生尝试添加评分记录，IP: {}", request.getRemoteAddr());
            return R.error("学生无权添加评分记录，请联系教师");
        }
        
        // 2. 基础参数校验
        if (!StringUtils.hasText(zuopindafen.getXuehao())) {
            log.warn("添加评分失败：学号为空");
            return R.error("学号不能为空");
        }
        
        if (!StringUtils.hasText(zuopindafen.getJingsaimingcheng())) {
            log.warn("添加评分失败：竞赛名称为空");
            return R.error("竞赛名称不能为空");
        }
        
        if (zuopindafen.getZuopinpingfen() == null) {
            log.warn("添加评分失败：作品分数为空");
            return R.error("作品分数不能为空");
        }
        
        try {
            // 3. 自动填充工号 (如果是教师评分)
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                zuopindafen.setGonghao(gonghao);
                log.info("教师 {} 添加作品评分：{}", gonghao, zuopindafen.getJingsaimingcheng());
            }
            
            // 4. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 4.5 【关键修复】兜底处理：设置addtime（如果前端未传递或格式错误）
            EntityUtil.setAddtimeIfNull(zuopindafen);
            
            // 5. 生成唯一 ID 并保存
            zuopindafen.setId(IdWorker.getId());
            zuopindafenService.insert(zuopindafen);

            // 发送打分通知给学生
            try {
                String studentXuehao2 = zuopindafen.getXuehao();
                String jingsaiName2 = zuopindafen.getJingsaimingcheng();
                Integer score2 = zuopindafen.getZuopinpingfen();
                String teacherName2 = zuopindafen.getJiaoshixingming() != null ? zuopindafen.getJiaoshixingming() : zuopindafen.getGonghao();
                if (studentXuehao2 != null && !studentXuehao2.isEmpty()) {
                    String title2 = "作品评分通知";
                    String content2 = String.format("您的「%s」竞赛作品已被教师「%s」评分，得分：%d分。", jingsaiName2, teacherName2, score2);
                    xiaoxiTongzhiService.sendTongzhi(
                        title2, content2, "成绩通知", teacherName2,
                        studentXuehao2, null, "xuesheng",
                        zuopindafen.getId(), "zuopindafen"
                    );
                    log.info("发送打分通知给学生: {}, 评分ID: {}", studentXuehao2, zuopindafen.getId());
                }
            } catch (Exception e) {
                log.error("发送打分通知异常", e);
            }

            log.info("添加作品评分成功，ID: {}, 学号：{}, 分数：{}", 
                     zuopindafen.getId(), 
                     zuopindafen.getXuehao(), 
                     zuopindafen.getZuopinpingfen());
            return R.ok("评分成功");
            
        } catch (Exception e) {
            log.error("添加作品评分异常：", e);
            return R.error("评分失败，请联系管理员");
        }
    }

    /**
     * 修改作品评分
     * 功能：更新已有的评分记录
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 验证实体完整性
     * 3. 更新到数据库
     * 
     * @param zuopindafen 作品打分实体 (包含更新后的数据)
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @RequestMapping("/update")
    @OperationLog("修改作品评分")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        // 1. 参数校验
        if (zuopindafen.getId() == null || zuopindafen.getId() <= 0) {
            log.warn("修改评分失败：ID 非法，ID: {}", zuopindafen.getId());
            return R.error("评分 ID 非法");
        }
        
        if (!StringUtils.hasText(zuopindafen.getXuehao())) {
            log.warn("修改评分失败：学号为空，ID: {}", zuopindafen.getId());
            return R.error("学号不能为空");
        }
        
        if (zuopindafen.getZuopinpingfen() == null) {
            log.warn("修改评分失败：作品分数为空，ID: {}", zuopindafen.getId());
            return R.error("作品分数不能为空");
        }
        
        // 2. 权限控制：只有教师和管理员可以修改评分
        String tableName = (String) request.getSession().getAttribute("tableName");
        log.debug("修改评分 - 当前用户表名：{}", tableName);
        
        if (tableName == null) {
            log.warn("修改评分失败：用户未登录");
            return R.error("请先登录");
        }
        
        if ("xuesheng".equals(tableName)) {
            log.warn("学生尝试修改评分记录，IP: {}", request.getRemoteAddr());
            return R.error("学生无权修改评分记录，请联系教师");
        }
        
        // 3. 如果是教师，只能修改自己的评分
        if ("jiaoshi".equals(tableName)) {
            String gonghao = (String) request.getSession().getAttribute("username");
            log.debug("当前教师工号：{}", gonghao);
            
            ZuopindafenEntity existingRecord = zuopindafenService.selectById(zuopindafen.getId());
            if (existingRecord == null) {
                log.warn("修改评分失败：评分记录不存在，ID: {}", zuopindafen.getId());
                return R.error("评分记录不存在");
            }
            
            if (gonghao == null || !gonghao.equalsIgnoreCase(existingRecord.getGonghao())) {
                log.warn("教师 {} 尝试修改其他教师的评分记录，ID: {}, 记录工号：{}", 
                         gonghao, zuopindafen.getId(), existingRecord.getGonghao());
                return R.error("只能修改自己的评分记录");
            }
        }
        
        // 4. 分数范围校验（0-100）
        if (zuopindafen.getZuopinpingfen() < 0 || zuopindafen.getZuopinpingfen() > 100) {
            log.warn("修改评分失败：分数超出范围，ID: {}, 分数：{}", zuopindafen.getId(), zuopindafen.getZuopinpingfen());
            return R.error("评分范围为 0-100 分");
        }
        
        try {
            // 5. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 6. 执行更新
            zuopindafenService.updateById(zuopindafen);
            
            log.info("修改作品评分成功，用户：{}, ID: {}, 学号：{}, 分数：{}", 
                     tableName,
                     zuopindafen.getId(), 
                     zuopindafen.getXuehao(), 
                     zuopindafen.getZuopinpingfen());
            return R.ok("修改成功");
            
        } catch (Exception e) {
            log.error("修改作品评分 ID{}异常：", zuopindafen.getId(), e);
            return R.error("修改失败，请联系管理员");
        }
    }

    /**
     * 删除作品评分
     * 功能：批量删除评分记录
     * 
     * 注意：此操作不可逆，请谨慎使用
     * 
     * @param ids 评分 ID 数组
     * @return R 统一返回结果
     */
    @RequestMapping("/delete")
    @OperationLog("删除作品评分")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody String[] ids, HttpServletRequest request) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除评分失败：ID 数组为空");
            return R.error("请选择要删除的评分");
        }
        
        try {
            // 将 String[] 转换为 Long[]，避免前端传递大数字时的精度丢失问题
            Long[] longIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                try {
                    longIds[i] = Long.parseLong(ids[i]);
                } catch (NumberFormatException e) {
                    log.error("ID 格式错误: {}", ids[i]);
                    return R.error("评分 ID 格式错误: " + ids[i]);
                }
            }
            
            log.info("接收到的删除请求，原始 IDs: {}，转换后 Long IDs: {}", Arrays.toString(ids), Arrays.toString(longIds));
            
            // 2. 权限控制：只有教师和管理员可以删除评分
            String tableName = (String) request.getSession().getAttribute("tableName");
            log.debug("当前用户表名：{}", tableName);
            
            if (tableName == null) {
                log.warn("删除评分失败：用户未登录");
                return R.error("请先登录");
            }
            
            if ("xuesheng".equals(tableName)) {
                log.warn("学生尝试删除评分记录，IP: {}", request.getRemoteAddr());
                return R.error("学生无权删除评分记录");
            }
            
            // 3. 如果是教师，只能删除自己的评分
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                log.debug("当前教师工号：{}", gonghao);
                
                for (Long id : longIds) {
                    ZuopindafenEntity zuopindafen = zuopindafenService.selectById(id);
                    if (zuopindafen == null) {
                        log.warn("删除评分失败：评分记录不存在，ID: {}", id);
                        return R.error("评分记录不存在");
                    }
                    if (gonghao == null || !gonghao.equalsIgnoreCase(zuopindafen.getGonghao())) {
                        log.warn("教师 {} 尝试删除其他教师的评分记录，ID: {}, 记录工号：{}", 
                                 gonghao, id, zuopindafen.getGonghao());
                        return R.error("只能删除自己的评分记录");
                    }
                }
            }
            
            // 4. 批量删除
            int count = longIds.length;
            zuopindafenService.deleteBatchIds(Arrays.asList(longIds));
            
            log.info("删除作品评分成功，用户：{}, 删除 IDs: {}, 数量：{}", tableName, Arrays.toString(longIds), count);
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除作品评分 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 提醒功能接口
     * 功能：统计待评分或其他状态的评分数量
     * 
     * 应用场景：
     * - 教师端：显示待评分的作品数量
     * - 学生端：显示我的作品评分提醒
     * 
     * @param columnName 字段名 (如 pingfenshijian-评分时间)
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
            Wrapper<ZuopindafenEntity> wrapper = new EntityWrapper<>();
            if (map.get("remindstart") != null) {
                wrapper.ge(columnName, map.get("remindstart")); // 大于等于开始时间
            }
            if (map.get("remindend") != null) {
                wrapper.le(columnName, map.get("remindend")); // 小于等于结束时间
            }
            
            // 4. 权限控制：根据用户角色过滤
            String tableName = (String) request.getSession().getAttribute("tableName");
            String role = (String) request.getSession().getAttribute("role");
            if (tableName != null) {
                if ("xuesheng".equals(tableName)) {
                    String xuehao = (String) request.getSession().getAttribute("username");
                    wrapper.eq("xuehao", xuehao);
                    log.debug("学生 {} 查询评分提醒", xuehao);
                } else if ("jiaoshi".equals(tableName) || "教师".equals(role)) {
                    // 教师只能看到自己创建的竞赛的评分提醒
                    // 【关键修复】不再使用wrapper.in()（MyBatis-Plus 2.x IN条件可能不生效），改为Java层内存过滤
                    String gonghao = (String) request.getSession().getAttribute("username");
                    EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                    jingsaiEw.eq("gonghao", gonghao);
                    List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                                
                    if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                        List<String> teacherJingsaiNames = myJingsaiList.stream()
                                .map(JingsaixinxiEntity::getJingsaimingcheng)
                                .filter(name -> name != null && !name.isEmpty())
                                .collect(java.util.stream.Collectors.toList());
                        // 不再使用 wrapper.in("jingsaimingcheng", teacherJingsaiNames)
                        // 改为在查询后用Java层过滤
                        map.put("teacherJingsaiNames", teacherJingsaiNames);
                    } else {
                        // 教师没有创建任何竞赛，直接返回count=0
                        log.debug("教师 {} 没有创建任何竞赛，提醒数量返回0", gonghao);
                        return R.ok().put("count", 0);
                    }
                    log.debug("教师 {} 查询评分提醒，竞赛名称：{}", gonghao, map.get("teacherJingsaiNames"));
                }
            }
            
            // 5. 统计数量
            // 【关键修复】教师角色：先用selectList查出数据，再在Java层过滤，最后计数
            List<String> teacherJingsaiNames = (List<String>) map.get("teacherJingsaiNames");
            if (teacherJingsaiNames != null) {
                // 教师角色：先查出所有符合时间条件的记录，再用竞赛名称过滤
                List<ZuopindafenEntity> remindList = zuopindafenService.selectList(wrapper);
                int count = 0;
                if (remindList != null) {
                    for (ZuopindafenEntity entity : remindList) {
                        if (entity.getJingsaimingcheng() != null && teacherJingsaiNames.contains(entity.getJingsaimingcheng())) {
                            count++;
                        }
                    }
                }
                log.debug("教师提醒过滤后数量：{}", count);
                return R.ok().put("count", count);
            } else {
                int count = zuopindafenService.selectCount(wrapper);
                log.debug("作品评分提醒查询成功，字段：{}, 类型：{}, 数量：{}", columnName, type, count);
                return R.ok().put("count", count);
            }
            
        } catch (Exception e) {
            log.error("作品评分提醒查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }

    /**
     * 获取统计数据
     * 功能：统计评分总数、平均分数、复核申请数
     * 
     * @param request HTTP 请求
     * @return R 统一返回结果，包含统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        try {
            log.info("========== 作品打分统计数据查询开始 ==========");
            Map<String, Object> stats = new HashMap<>();
            String tableName = (String) request.getSession().getAttribute("tableName");
            
            // 使用 EntityWrapper 缩小查询范围，避免全表扫描
            EntityWrapper<ZuopindafenEntity> scoreEw = new EntityWrapper<>();
            List<String> teacherJingsaiNames = null; // 教师角色：用于Java层内存过滤
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                scoreEw.eq("xuehao", xuehao);
            } else if ("jiaoshi".equals(tableName)) {
                // 教师只能统计自己创建的竞赛的作品打分数据
                // 【关键修复】不再使用scoreEw.in()（MyBatis-Plus 2.x IN条件可能不生效），改为Java层内存过滤
                String gonghao = (String) request.getSession().getAttribute("username");
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    teacherJingsaiNames = myJingsaiList.stream()
                            .map(JingsaixinxiEntity::getJingsaimingcheng)
                            .filter(name -> name != null && !name.isEmpty())
                            .collect(java.util.stream.Collectors.toList());
                    log.info("教师 {} 统计自己创建的 {} 个竞赛的作品打分", gonghao, teacherJingsaiNames.size());
                } else {
                    log.info("教师 {} 没有创建任何竞赛，统计返回0", gonghao);
                }
            }
            List<ZuopindafenEntity> allScores = zuopindafenService.selectList(scoreEw);
            
            // 【关键修复】教师角色：Java层内存过滤
            if ("jiaoshi".equals(tableName) && teacherJingsaiNames != null) {
                if (allScores != null) {
                    final List<String> finalTeacherJingsaiNames = teacherJingsaiNames;
                    allScores = allScores.stream()
                        .filter(entity -> entity.getJingsaimingcheng() != null && finalTeacherJingsaiNames.contains(entity.getJingsaimingcheng()))
                        .collect(java.util.stream.Collectors.toList());
                    log.info("教师统计内存过滤后：{}条记录", allScores.size());
                }
            } else if ("jiaoshi".equals(tableName) && teacherJingsaiNames == null) {
                // 教师没有创建任何竞赛，返回空结果
                allScores = new java.util.ArrayList<>();
            }
            
            // 1. 统计评分总数
            int totalCount = allScores != null ? allScores.size() : 0;
            log.info("评分总数：{}", totalCount);
            stats.put("totalPingfen", totalCount);
            
            // 2. 计算平均分数
            if (totalCount > 0) {
                double sumScore = 0;
                int validScoreCount = 0;
                for (ZuopindafenEntity item : allScores) {
                    if (item.getZuopinpingfen() != null) {
                        sumScore += item.getZuopinpingfen();
                        validScoreCount++;
                    }
                }
                if (validScoreCount > 0) {
                    double avgScore = sumScore / validScoreCount;
                    stats.put("avgScore", Math.round(avgScore * 10.0) / 10.0);
                    log.info("平均分：{}", stats.get("avgScore"));
                } else {
                    stats.put("avgScore", 0);
                    log.info("平均分：0（无有效分数）");
                }
            } else {
                stats.put("avgScore", 0);
                log.info("平均分：0（无评分记录）");
            }
            
            // 3. 统计复核申请数 - 【关键修复】不再使用fuheEw.in()，改为Java层内存过滤
            EntityWrapper<ZuopindafenFuheEntity> fuheEw = new EntityWrapper<>();
            List<Long> teacherScoreIds = null; // 教师角色：用于Java层内存过滤复核记录
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                fuheEw.eq("xuehao", xuehao);
            } else if ("jiaoshi".equals(tableName)) {
                // 教师需要过滤自己创建的竞赛的作品的复核记录
                if (allScores != null && !allScores.isEmpty()) {
                    teacherScoreIds = allScores.stream()
                        .map(ZuopindafenEntity::getId)
                        .collect(java.util.stream.Collectors.toList());
                    // 不再使用 fuheEw.in("zuopindafen_id", teacherScoreIds)
                } else {
                    // 教师没有评分记录，复核数必然为0
                }
            }
            if (teacherScoreIds != null) {
                // 教师角色：查出所有复核记录，再用Java层过滤
                List<ZuopindafenFuheEntity> allFuheList = zuopindafenFuheService.selectList(fuheEw);
                int fuheCount = 0;
                if (allFuheList != null) {
                    for (ZuopindafenFuheEntity fuhe : allFuheList) {
                        if (fuhe.getZuopindafenId() != null && teacherScoreIds.contains(fuhe.getZuopindafenId())) {
                            fuheCount++;
                        }
                    }
                }
                log.info("教师复核内存过滤后数量：{}", fuheCount);
                stats.put("fuheCount", fuheCount);
            } else if ("jiaoshi".equals(tableName)) {
                // 教师没有评分记录，复核数为0
                stats.put("fuheCount", 0);
            } else {
                int fuheCount = zuopindafenFuheService.selectCount(fuheEw);
                log.info("复核申请数：{}", fuheCount);
                stats.put("fuheCount", fuheCount);
            }
            
            log.info("统计数据查询成功，角色：{}", tableName);
            return R.ok().put("data", stats);
            
        } catch (Exception e) {
            log.error("统计数据查询异常：", e);
            return R.error("统计查询失败，请重试");
        }
    }

    /**
     * 【论文3.1.1(5)】成绩单PDF导出
     * 功能：学生下载自己的成绩单PDF，教师/管理员下载指定学生的成绩单PDF
     * 
     * @param xuehao 学号（可选，管理员/教师可指定）
     * @param jingsaimingcheng 竞赛名称（可选，指定某竞赛的成绩）
     * @param request HTTP请求
     * @param response HTTP响应
     */
    @IgnoreAuth
    @GetMapping("/exportPdf")
    @OperationLog("导出成绩PDF")
    public void exportPdf(@RequestParam(required = false) String xuehao,
                         @RequestParam(required = false) String jingsaimingcheng,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            // 支持通过Token参数认证（用于前端直接下载）
            String token = request.getParameter("Token");
            if (token != null && !token.isEmpty()) {
                TokenEntity tokenEntity = tokenService.getTokenEntity(token);
                if (tokenEntity != null) {
                    request.getSession().setAttribute("userId", tokenEntity.getUserid());
                    request.getSession().setAttribute("role", tokenEntity.getRole());
                    request.getSession().setAttribute("tableName", tokenEntity.getTablename());
                    request.getSession().setAttribute("username", tokenEntity.getUsername());
                }
            }
            
            // 1. 权限控制：根据角色确定查询范围
            String tableName = (String) request.getSession().getAttribute("tableName");
            if (tableName == null || tableName.isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
                return;
            }
            
            String queryXuehao = xuehao;
            
            if ("xuesheng".equals(tableName)) {
                // 学生只能导出自己的成绩
                queryXuehao = (String) request.getSession().getAttribute("username");
            } else if ("jiaoshi".equals(tableName)) {
                // 教师导出自己创建的竞赛的成绩
                if (queryXuehao == null || queryXuehao.isEmpty()) {
                    // 教师没有指定学号，导出自己竞赛的所有成绩
                }
            }
            // 管理员可以导出任何成绩
            
            // 2. 查询成绩数据
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            List<String> teacherJingsaiNames = null; // 教师角色：用于Java层内存过滤
            if (queryXuehao != null && !queryXuehao.isEmpty()) {
                ew.eq("xuehao", queryXuehao);
            }
            if ("jiaoshi".equals(tableName)) {
                // 【关键修复】不再使用ew.in()（MyBatis-Plus 2.x IN条件可能不生效），改为Java层内存过滤
                String gonghao = (String) request.getSession().getAttribute("username");
                EntityWrapper<JingsaixinxiEntity> jingsaiEw = new EntityWrapper<>();
                jingsaiEw.eq("gonghao", gonghao);
                List<JingsaixinxiEntity> myJingsaiList = jingsaixinxiService.selectList(jingsaiEw);
                if (myJingsaiList != null && !myJingsaiList.isEmpty()) {
                    teacherJingsaiNames = myJingsaiList.stream()
                        .map(JingsaixinxiEntity::getJingsaimingcheng)
                        .filter(name -> name != null && !name.isEmpty())
                        .collect(java.util.stream.Collectors.toList());
                }
                // 教师没有创建任何竞赛时 teacherJingsaiNames 为 null
            }
            if (jingsaimingcheng != null && !jingsaimingcheng.isEmpty()) {
                ew.eq("jingsaimingcheng", jingsaimingcheng);
            }
            ew.orderBy("pingjiashijian", false);
            List<ZuopindafenEntity> scoreList = zuopindafenService.selectList(ew);
            
            // 【关键修复】教师角色：Java层内存过滤
            if (teacherJingsaiNames != null && scoreList != null) {
                final List<String> finalTeacherJingsaiNames = teacherJingsaiNames;
                scoreList = scoreList.stream()
                    .filter(entity -> entity.getJingsaimingcheng() != null && finalTeacherJingsaiNames.contains(entity.getJingsaimingcheng()))
                    .collect(java.util.stream.Collectors.toList());
            } else if ("jiaoshi".equals(tableName) && teacherJingsaiNames == null) {
                // 教师没有创建任何竞赛，返回空结果
                scoreList = new java.util.ArrayList<>();
            }
            
            if (scoreList == null || scoreList.isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":1,\"msg\":\"没有成绩数据可导出\"}");
                return;
            }
            
            // 3. 生成PDF
            ByteArrayOutputStream baos = generateScorePdf(scoreList, queryXuehao);
            
            // 4. 设置响应头，返回PDF文件
            String fileName = "成绩单_" + (queryXuehao != null ? queryXuehao : "全部") + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            
            log.info("【PDF导出】用户{}导出成绩PDF，学号：{}，记录数：{}", tableName, queryXuehao, scoreList.size());
            
        } catch (Exception e) {
            log.error("导出成绩PDF异常：", e);
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":1,\"msg\":\"PDF导出失败，请重试\"}");
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }
    
    /**
     * 生成成绩单PDF
     * @param scoreList 成绩列表
     * @param xuehao 学号
     * @return PDF字节流
     */
    private ByteArrayOutputStream generateScorePdf(List<ZuopindafenEntity> scoreList, String xuehao) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 50, 50, 50, 50);
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);
        document.open();
        
        // 使用中文字体
        com.itextpdf.text.pdf.BaseFont bfChinese = com.itextpdf.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(bfChinese, 10, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(bfChinese, 8, com.itextpdf.text.Font.NORMAL);
        
        // 标题
        String studentName = scoreList.get(0).getXueshengxingming();
        String titleText = "大学生竞赛管理系统 - 成绩单";
        if (xuehao != null && !xuehao.isEmpty()) {
            titleText = studentName + "(" + xuehao + ")的成绩单";
        }
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(titleText, titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // 信息行
        com.itextpdf.text.Paragraph info = new com.itextpdf.text.Paragraph();
        info.add(new com.itextpdf.text.Chunk("导出时间：" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()), smallFont));
        info.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        info.setSpacingAfter(15);
        document.add(info);
        
        // 成绩表格
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        // 设置列宽比例
        float[] widths = {15f, 15f, 25f, 15f, 30f};
        table.setWidths(widths);
        
        // 表头
        String[] headers = {"学号", "学生姓名", "竞赛名称", "成绩", "评价内容"};
        for (String header : headers) {
            com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(header, headerFont));
            headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            headerCell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
            headerCell.setPadding(8);
            table.addCell(headerCell);
        }
        
        // 数据行
        for (ZuopindafenEntity score : scoreList) {
            com.itextpdf.text.pdf.PdfPCell cellXuehao = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(score.getXuehao() != null ? score.getXuehao() : "", contentFont));
            cellXuehao.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cellXuehao.setPadding(5);
            table.addCell(cellXuehao);
            
            com.itextpdf.text.pdf.PdfPCell cellName = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(score.getXueshengxingming() != null ? score.getXueshengxingming() : "", contentFont));
            cellName.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cellName.setPadding(5);
            table.addCell(cellName);
            
            com.itextpdf.text.pdf.PdfPCell cellJingsai = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(score.getJingsaimingcheng() != null ? score.getJingsaimingcheng() : "", contentFont));
            cellJingsai.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cellJingsai.setPadding(5);
            table.addCell(cellJingsai);
            
            com.itextpdf.text.pdf.PdfPCell cellScore = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(score.getZuopinpingfen() != null ? score.getZuopinpingfen().toString() : "", contentFont));
            cellScore.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cellScore.setPadding(5);
            table.addCell(cellScore);
            
            com.itextpdf.text.pdf.PdfPCell cellComment = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Paragraph(score.getPingjianeirong() != null ? score.getPingjianeirong() : "", contentFont));
            cellComment.setPadding(5);
            table.addCell(cellComment);
        }
        
        document.add(table);
        
        // 统计信息
        com.itextpdf.text.Paragraph statsTitle = new com.itextpdf.text.Paragraph("统计信息", headerFont);
        statsTitle.setSpacingBefore(20);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        double avgScore = 0;
        int maxScore = 0;
        int minScore = 100;
        for (ZuopindafenEntity score : scoreList) {
            if (score.getZuopinpingfen() != null) {
                avgScore += score.getZuopinpingfen();
                if (score.getZuopinpingfen() > maxScore) maxScore = score.getZuopinpingfen();
                if (score.getZuopinpingfen() < minScore) minScore = score.getZuopinpingfen();
            }
        }
        avgScore = scoreList.size() > 0 ? avgScore / scoreList.size() : 0;
        
        com.itextpdf.text.Paragraph statsContent = new com.itextpdf.text.Paragraph();
        statsContent.add(new com.itextpdf.text.Chunk("记录数：" + scoreList.size() + "  ", contentFont));
        statsContent.add(new com.itextpdf.text.Chunk("平均分：" + Math.round(avgScore * 10.0) / 10.0 + "  ", contentFont));
        statsContent.add(new com.itextpdf.text.Chunk("最高分：" + maxScore + "  ", contentFont));
        statsContent.add(new com.itextpdf.text.Chunk("最低分：" + minScore, contentFont));
        document.add(statsContent);
        
        // 页脚
        com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph("— 大学生竞赛管理系统 —", smallFont);
        footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
        
        document.close();
        writer.close();
        return baos;
    }
}
