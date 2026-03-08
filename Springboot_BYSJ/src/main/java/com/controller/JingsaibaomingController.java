package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JingsaibaomingEntity;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaibaomingView;
import com.service.JingsaibaomingService;
import com.service.JingsaixinxiService;
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
import java.util.Map;

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
        try {
            // 1. 权限控制：根据用户角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                // 教师只能查看自己发布的竞赛的报名
                String gonghao = (String) request.getSession().getAttribute("username");
                jingsaibaoming.setGonghao(gonghao);
                log.debug("教师 {} 查询所教竞赛的报名列表", gonghao);
            } else if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的报名
                String xuehao = (String) request.getSession().getAttribute("username");
                jingsaibaoming.setXuehao(xuehao);
                log.debug("学生 {} 查询个人报名列表", xuehao);
            }
            
            // 2. 构建查询条件
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            
            // 3. 执行分页查询
            PageUtils page = jingsaibaomingService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jingsaibaoming), params), params)
            );
            
            return R.ok().put("data", page);
            
        } catch (Exception e) {
            log.error("报名分页查询异常：", e);
            return R.error("查询失败，请重试");
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
            // 1. 构建查询条件
            EntityWrapper<JingsaibaomingEntity> ew = new EntityWrapper<>();
            
            // 2. 执行分页查询
            PageUtils page = jingsaibaomingService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jingsaibaoming), params), params)
            );
            
            return R.ok().put("data", page);
            
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
    public R save(@RequestBody JingsaibaomingEntity jingsaibaoming, HttpServletRequest request) {
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
            
            if (jingsai != null && jingsai.getJingsaishijian() != null) {
                // 如果当前时间超过竞赛时间，则不允许报名
                if (jingsai.getJingsaishijian().before(new Date())) {
                    log.warn("报名失败：竞赛 {} 已结束", jingsai.getJingsaimingcheng());
                    return R.error("该竞赛已结束，无法提交申请");
                }
            }
        }
        
        try {
            // 2. 自动填充学号 (如果是学生自己报名)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                jingsaibaoming.setXuehao(xuehao);
                
                // 可选：自动填充学生姓名
                // jingsaibaoming.setXueshengxingming(...);
                
                log.info("学生 {} 报名竞赛：{}", xuehao, jingsaibaoming.getJingsaimingcheng());
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
            
            log.info("保存报名信息成功，ID: {}, 学号：{}, 竞赛：{}", 
                     jingsaibaoming.getId(), 
                     jingsaibaoming.getXuehao(), 
                     jingsaibaoming.getJingsaimingcheng());
            return R.ok("报名成功");
            
        } catch (Exception e) {
            log.error("保存报名信息异常：", e);
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
        
        try {
            // 2. 自动填充学号 (如果是学生自己报名)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName)) {
                String xuehao = (String) request.getSession().getAttribute("username");
                jingsaibaoming.setXuehao(xuehao);
                log.info("学生 {} 添加竞赛报名：{}", xuehao, jingsaibaoming.getJingsaimingcheng());
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(jingsaibaoming);
            
            // 4. 生成唯一 ID 并保存
            jingsaibaoming.setId(IdWorker.getId());
            jingsaibaomingService.insert(jingsaibaoming);
            
            log.info("添加报名信息成功，ID: {}, 学号：{}, 竞赛：{}", 
                     jingsaibaoming.getId(), 
                     jingsaibaoming.getXuehao(), 
                     jingsaibaoming.getJingsaimingcheng());
            return R.ok("报名成功");
            
        } catch (Exception e) {
            log.error("添加报名信息异常：", e);
            return R.error("报名失败，请联系管理员");
        }
    }

    /**
     * 修改报名信息
     * 功能：更新已有的报名信息
     * 
     * 业务逻辑：
     * 1. 验证必填字段
     * 2. 验证实体完整性
     * 3. 更新到数据库
     * 
     * @param jingsaibaoming 报名信息实体 (包含更新后的数据)
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @OperationLog("修改竞赛报名信息")
    @RequestMapping("/update")
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
            // 2. 实体校验
            ValidatorUtils.validateEntity(jingsaibaoming);
            
            // 3. 执行更新
            jingsaibaomingService.updateById(jingsaibaoming);
            
            log.info("修改报名信息成功，ID: {}, 学号：{}", 
                     jingsaibaoming.getId(), jingsaibaoming.getXuehao());
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
    public R delete(@RequestBody Long[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除报名失败：ID 数组为空");
            return R.error("请选择要删除的报名");
        }
        
        try {
            // 2. 批量删除
            jingsaibaomingService.deleteBatchIds(Arrays.asList(ids));
            
            log.info("删除报名信息成功，IDs: {}", Arrays.toString(ids));
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除报名 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
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
}
