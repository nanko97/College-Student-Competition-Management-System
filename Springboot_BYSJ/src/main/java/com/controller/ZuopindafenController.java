package com.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.ZuopindafenEntity;
import com.entity.view.ZuopindafenView;
import com.service.ZuopindafenService;
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

    /**
     * 后端分页列表查询
     * 功能：带权限控制的作品打分信息查询
     * 
     * 权限逻辑：
     * - 教师角色：只能查看自己评分的作品
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
        try {
            // 1. 权限控制：根据用户角色过滤数据
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("xuesheng".equals(tableName)) {
                // 学生只能查看自己的作品得分
                String xuehao = (String) request.getSession().getAttribute("username");
                zuopindafen.setXuehao(xuehao);
                log.debug("学生 {} 查询个人作品得分", xuehao);
            } else if ("jiaoshi".equals(tableName)) {
                // 教师只能查看自己评分的作品
                String gonghao = (String) request.getSession().getAttribute("username");
                zuopindafen.setGonghao(gonghao);
                log.debug("教师 {} 查询评分记录", gonghao);
            }
            
            // 2. 构建查询条件
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            
            // 3. 执行分页查询
            PageUtils page = zuopindafenService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zuopindafen), params), params)
            );
            
            return R.ok().put("data", page);
            
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
            // 1. 构建查询条件
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            
            // 2. 执行分页查询
            PageUtils page = zuopindafenService.queryPage(
                params, 
                MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zuopindafen), params), params)
            );
            
            return R.ok().put("data", page);
            
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
    public R list(ZuopindafenEntity zuopindafen) {
        try {
            // 1. 构建查询条件 (精确匹配)
            EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
            ew.allEq(MPUtil.allEQMapPre(zuopindafen, "zuopindafen"));
            
            // 2. 查询列表
            return R.ok().put("data", zuopindafenService.selectListView(ew));
            
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
     * @return R 统一返回结果，包含评分详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
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
     * @return R 统一返回结果，包含评分详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
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
    public R save(@RequestBody ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        // 1. 基础参数校验
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
        
        try {
            // 2. 自动填充工号 (如果是教师评分)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                zuopindafen.setGonghao(gonghao);
                log.info("教师 {} 为竞赛 {} 的作品评分：{}", 
                         gonghao, zuopindafen.getJingsaimingcheng(), zuopindafen.getZuopinpingfen());
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 4. 生成唯一 ID 并保存
            zuopindafen.setId(IdWorker.getId());
            zuopindafenService.insert(zuopindafen);
            
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
    public R add(@RequestBody ZuopindafenEntity zuopindafen, HttpServletRequest request) {
        // 1. 基础参数校验
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
            // 2. 自动填充工号 (如果是教师评分)
            String tableName = (String) request.getSession().getAttribute("tableName");
            if ("jiaoshi".equals(tableName)) {
                String gonghao = (String) request.getSession().getAttribute("username");
                zuopindafen.setGonghao(gonghao);
                log.info("教师 {} 添加作品评分：{}", gonghao, zuopindafen.getJingsaimingcheng());
            }
            
            // 3. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 4. 生成唯一 ID 并保存
            zuopindafen.setId(IdWorker.getId());
            zuopindafenService.insert(zuopindafen);
            
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
        
        try {
            // 2. 实体校验
            ValidatorUtils.validateEntity(zuopindafen);
            
            // 3. 执行更新
            zuopindafenService.updateById(zuopindafen);
            
            log.info("修改作品评分成功，ID: {}, 学号：{}, 分数：{}", 
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
    public R delete(@RequestBody Long[] ids) {
        // 1. 参数校验
        if (ids == null || ids.length == 0) {
            log.warn("删除评分失败：ID 数组为空");
            return R.error("请选择要删除的评分");
        }
        
        try {
            // 2. 批量删除
            zuopindafenService.deleteBatchIds(Arrays.asList(ids));
            
            log.info("删除作品评分成功，IDs: {}", Arrays.toString(ids));
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除作品评分 ID{}异常：", Arrays.toString(ids), e);
            return R.error("删除失败，请联系管理员");
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
            if (tableName != null) {
                if ("xuesheng".equals(tableName)) {
                    String xuehao = (String) request.getSession().getAttribute("username");
                    wrapper.eq("xuehao", xuehao);
                    log.debug("学生 {} 查询评分提醒", xuehao);
                } else if ("jiaoshi".equals(tableName)) {
                    String gonghao = (String) request.getSession().getAttribute("username");
                    wrapper.eq("gonghao", gonghao);
                    log.debug("教师 {} 查询评分提醒", gonghao);
                }
            }
            
            // 5. 统计数量
            int count = zuopindafenService.selectCount(wrapper);
            
            log.debug("作品评分提醒查询成功，字段：{}, 类型：{}, 数量：{}", columnName, type, count);
            return R.ok().put("count", count);
            
        } catch (Exception e) {
            log.error("作品评分提醒查询异常：", e);
            return R.error("查询失败，请重试");
        }
    }
}
