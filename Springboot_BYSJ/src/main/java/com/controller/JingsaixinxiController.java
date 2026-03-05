package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaixinxiView;
import com.service.impl.JingsaixinxiServiceImpl;
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
public class JingsaixinxiController {
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;

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
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, 
                  JingsaixinxiEntity jingsaixinxi,
                  HttpServletRequest request) {
        try {
            // 1. 权限控制：如果是教师登录，只能查看自己发布的竞赛
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                jingsaixinxi.setGonghao(gonghao);
                log.debug("教师 {} 查询个人竞赛列表", gonghao);
            }
            
            // 2. 构建查询条件 (支持模糊查询、区间查询、排序)
            EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
            
            // 3. 执行分页查询
            PageUtils page = jingsaixinxiService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jingsaixinxi), params), params)
            );
            
            return R.ok().put("data", page);
            
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
            
            return R.ok().put("data", page);
            
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
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        try {
            // 1. 参数校验
            if (id == null || id <= 0) {
                log.warn("查询竞赛详情失败：ID 非法，ID: {}", id);
                return R.error("竞赛 ID 非法");
            }
            
            // 2. 查询竞赛信息 (带缓存)
            JingsaixinxiEntity jingsaixinxi = ((JingsaixinxiServiceImpl) jingsaixinxiService)
                .selectByIdWithCache(id);
            
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
    @RequestMapping("/save")
    public R save(@RequestBody JingsaixinxiEntity jingsaixinxi, HttpServletRequest request) {
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
                jingsaixinxi.setGonghao(gonghao);
                log.info("教师 {} 发布新竞赛：{}", gonghao, jingsaixinxi.getJingsaimingcheng());
            }
            
            // 3. 实体校验 (使用验证工具类)
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 4. 生成唯一 ID 并保存
            jingsaixinxi.setId(IdWorker.getId());
            jingsaixinxiService.insert(jingsaixinxi);
            
            log.info("保存竞赛信息成功，ID: {}, 名称：{}", 
                     jingsaixinxi.getId(), jingsaixinxi.getJingsaimingcheng());
            return R.ok("保存成功");
            
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
                jingsaixinxi.setGonghao(gonghao);
                log.info("教师 {} 添加新竞赛：{}", gonghao, jingsaixinxi.getJingsaimingcheng());
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
     * 3. 更新到数据库
     * 
     * @param jingsaixinxi 竞赛信息实体 (包含更新后的数据)
     * @param request HTTP 请求
     * @return R 统一返回结果
     */
    @RequestMapping("/update")
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
            // 2. 实体校验
            ValidatorUtils.validateEntity(jingsaixinxi);
            
            // 3. 执行更新
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
     * 
     * @param ids 竞赛 ID 数组
     * @return R 统一返回结果
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除竞赛失败：ID 数组为空");
            return R.error("请选择要删除的竞赛");
        }
        
        try {
            // 2. 批量删除
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
}
