package com.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.entity.JiaoshiEntity;
import com.entity.ZuopindafenEntity;
import com.service.JiaoshiService;
import com.service.ZuopindafenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.ZuopindafenFuheEntity;
import com.entity.view.ZuopindafenFuheView;
import com.service.ZuopindafenFuheService;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 作品打分复核申请Controller
 */
@RestController
@RequestMapping("/zuopindafenfuhe")
public class ZuopindafenFuheController {

    private static final Logger logger = LoggerFactory.getLogger(ZuopindafenFuheController.class);

    @Autowired
    private ZuopindafenFuheService zuopindafenFuheService;

    @Autowired
    private ZuopindafenService zuopindafenService;
    
    @Autowired
    private JiaoshiService jiaoshiService;

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("page方法:,,Controller:{},,params:{}", this.getClass().getName(), params);
        
        // 权限控制：学生只能查看自己的复核记录
        String xuehao = (String) params.get("xuehao");
        if (xuehao != null && !xuehao.isEmpty()) {
            logger.info("学生复核记录过滤：只查询学号 {} 的记录", xuehao);
        }
        
        PageUtils page = zuopindafenFuheService.queryPage(params);
        
        // 关联查询教师信息，确保显示真实的教师数据
        if (page != null && page.getList() != null) {
            for (Object obj : page.getList()) {
                ZuopindafenFuheEntity fuhe = (ZuopindafenFuheEntity) obj;
                
                // 如果有审核工号，从教师表查询真实信息
                if (fuhe.getShenheGonghao() != null && !fuhe.getShenheGonghao().isEmpty()) {
                    EntityWrapper<JiaoshiEntity> teacherEw = new EntityWrapper<>();
                    teacherEw.eq("gonghao", fuhe.getShenheGonghao());
                    List<JiaoshiEntity> teacherList = jiaoshiService.selectList(teacherEw);
                    
                    if (teacherList != null && !teacherList.isEmpty()) {
                        JiaoshiEntity teacher = teacherList.get(0);
                        // 使用教师表中的真实姓名（如果为空则保持原值）
                        if (teacher.getJiaoshixingming() != null && !teacher.getJiaoshixingming().isEmpty()) {
                            fuhe.setShenheJiaoshi(teacher.getJiaoshixingming());
                        }
                    }
                }
            }
        }
        
        // 【新增】返回统计数据
        Map<String, Object> statistics = getStatistics(xuehao);
        
        return R.ok().put("data", page).put("page", page).put("statistics", statistics);
    }
    
    /**
     * 获取统计数据
     */
    private Map<String, Object> getStatistics(String xuehao) {
        Map<String, Object> statistics = new HashMap<>();
        
        EntityWrapper<ZuopindafenFuheEntity> ew = new EntityWrapper<>();
        if (xuehao != null && !xuehao.isEmpty()) {
            ew.eq("xuehao", xuehao);
        }
        
        // 总数
        Integer total = zuopindafenFuheService.selectCount(ew);
        statistics.put("total", total);
        
        // 待审核
        EntityWrapper<ZuopindafenFuheEntity> pendingEw = new EntityWrapper<>();
        if (xuehao != null && !xuehao.isEmpty()) {
            pendingEw.eq("xuehao", xuehao);
        }
        pendingEw.eq("fuhe_status", "待审核");
        statistics.put("pendingCount", zuopindafenFuheService.selectCount(pendingEw));
        
        // 已通过
        EntityWrapper<ZuopindafenFuheEntity> approvedEw = new EntityWrapper<>();
        if (xuehao != null && !xuehao.isEmpty()) {
            approvedEw.eq("xuehao", xuehao);
        }
        approvedEw.eq("fuhe_status", "已通过");
        statistics.put("approvedCount", zuopindafenFuheService.selectCount(approvedEw));
        
        // 已驳回
        EntityWrapper<ZuopindafenFuheEntity> rejectedEw = new EntityWrapper<>();
        if (xuehao != null && !xuehao.isEmpty()) {
            rejectedEw.eq("xuehao", xuehao);
        }
        rejectedEw.eq("fuhe_status", "已驳回");
        statistics.put("rejectedCount", zuopindafenFuheService.selectCount(rejectedEw));
        
        return statistics;
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        logger.debug("info方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
        ZuopindafenFuheEntity zuopindafenFuhe = zuopindafenFuheService.selectById(id);
        return R.ok().put("data", zuopindafenFuhe);
    }

    // 最大复核次数限制
    private static final int MAX_REVIEW_COUNT = 2;
    
    /**
     * 后端保存
     */
    @OperationLog("保存成绩复核申请")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody ZuopindafenFuheEntity zuopindafenFuhe) {
        logger.debug("save方法:,,Controller:{},,zuopindafenFuhe:{}", this.getClass().getName(), zuopindafenFuhe);
        
        // 【防重复检查】根据作品打分ID自动获取学号和学生姓名
        if (zuopindafenFuhe.getZuopindafenId() != null) {
            ZuopindafenEntity zuopindafen = zuopindafenService.selectById(zuopindafenFuhe.getZuopindafenId());
            if (zuopindafen != null) {
                zuopindafenFuhe.setXuehao(zuopindafen.getXuehao());
                zuopindafenFuhe.setXueshengxingming(zuopindafen.getXueshengxingming());
                logger.info("自动填充学号：{}，学生姓名：{}", zuopindafen.getXuehao(), zuopindafen.getXueshengxingming());
                
                // 【关键检查】查询该学生对该作品的所有历史复核记录
                EntityWrapper<ZuopindafenFuheEntity> checkEw = new EntityWrapper<>();
                checkEw.eq("zuopindafen_id", zuopindafenFuhe.getZuopindafenId());
                checkEw.eq("xuehao", zuopindafen.getXuehao());
                checkEw.orderBy("id", true); // 按ID升序，最新的在最后
                List<ZuopindafenFuheEntity> existingFuheList = zuopindafenFuheService.selectList(checkEw);
                
                // 【次数限制检查】统计历史提交次数
                if (existingFuheList != null && existingFuheList.size() >= MAX_REVIEW_COUNT) {
                    logger.warn("学生 {} 对作品 {} 已提交 {} 次复核申请，已达上限，拒绝提交", 
                            zuopindafen.getXuehao(), zuopindafenFuhe.getZuopindafenId(), existingFuheList.size());
                    return R.error("您对该作品的复核申请次数已达上限（最多" + MAX_REVIEW_COUNT + "次），无法再次提交");
                }
                
                if (existingFuheList != null && !existingFuheList.isEmpty()) {
                    // 获取最新的复核记录
                    ZuopindafenFuheEntity latestFuhe = existingFuheList.get(existingFuheList.size() - 1);
                    String status = latestFuhe.getFuheStatus();
                    
                    // 如果最新的复核记录状态不是"已驳回"，则不允许重复提交
                    if (!"已驳回".equals(status)) {
                        logger.warn("学生 {} 对作品 {} 的最新复核申请状态为【{}】，请勿重复提交", 
                                zuopindafen.getXuehao(), zuopindafenFuhe.getZuopindafenId(), status);
                        return R.error("您对该作品的最新复核申请状态为【" + status + "】，请勿重复提交");
                    }
                    
                    // 如果是已驳回状态，且未达到次数上限，创建新的复核记录
                    logger.info("学生 {} 重新提交复核申请（原申请已被驳回），创建新记录", zuopindafen.getXuehao());
                }
            }
        }
        
        // 首次提交或驳回后重新提交，创建新记录
        zuopindafenFuheService.insert(zuopindafenFuhe);
        return R.ok("复核申请已提交，请等待审核");
    }

    /**
     * 后端保存（add别名）
     */
    @PostMapping("/add")
    public R add(@RequestBody ZuopindafenFuheEntity zuopindafenFuhe) {
        return save(zuopindafenFuhe);
    }

    /**
     * 修改
     */
    @OperationLog("修改成绩复核申请")
    @RequestMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody ZuopindafenFuheEntity zuopindafenFuhe) {
        logger.debug("update方法:,,Controller:{},,zuopindafenFuhe:{}", this.getClass().getName(), zuopindafenFuhe);
        
        // 如果是审核通过并重新打分，更新原成绩
        if ("已通过".equals(zuopindafenFuhe.getFuheStatus()) && zuopindafenFuhe.getXinChengji() != null) {
            ZuopindafenEntity zuopindafen = zuopindafenService.selectById(zuopindafenFuhe.getZuopindafenId());
            if (zuopindafen != null) {
                try {
                    zuopindafen.setZuopinpingfen(Integer.parseInt(zuopindafenFuhe.getXinChengji()));
                    zuopindafenService.updateById(zuopindafen);
                } catch (NumberFormatException e) {
                    logger.error("成绩格式错误：{}", zuopindafenFuhe.getXinChengji());
                }
            }
        }
        
        zuopindafenFuheService.updateById(zuopindafenFuhe);
        return R.ok();
    }

    /**
     * 删除
     */
    @OperationLog("删除成绩复核申请")
    @RequestMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        logger.debug("delete:,,Controller:{},,ids:{}", this.getClass().getName(), ids);
        zuopindafenFuheService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 审核复核申请（教师使用）
     */
    @OperationLog("审核成绩复核申请")
    @PostMapping("/shenhe")
    @Transactional(rollbackFor = Exception.class)
    public R shenhe(@RequestBody ZuopindafenFuheEntity zuopindafenFuhe) {
        logger.debug("审核复核申请:{}", zuopindafenFuhe);
        
        // 获取当前登录教师信息
        // 注意：shenheGonghao和shenheJiaoshi字段已由前端传递
        
        // 更新复核记录
        zuopindafenFuhe.setShenheShijian(new Date());
        
        // 确保审核教师信息不为空
        if (zuopindafenFuhe.getShenheGonghao() != null && !zuopindafenFuhe.getShenheGonghao().isEmpty()) {
            logger.info("审核教师工号：{}，姓名：{}", zuopindafenFuhe.getShenheGonghao(), zuopindafenFuhe.getShenheJiaoshi());
        }
        
        zuopindafenFuheService.updateById(zuopindafenFuhe);
        
        // 如果审核通过且有新成绩，更新原成绩
        if ("已通过".equals(zuopindafenFuhe.getFuheStatus()) && zuopindafenFuhe.getXinChengji() != null) {
            ZuopindafenEntity zuopindafen = zuopindafenService.selectById(zuopindafenFuhe.getZuopindafenId());
            if (zuopindafen != null) {
                try {
                    zuopindafen.setZuopinpingfen(Integer.parseInt(zuopindafenFuhe.getXinChengji()));
                    zuopindafenService.updateById(zuopindafen);
                    logger.info("成绩已更新：{} -> {}", zuopindafenFuhe.getYuanChengji(), zuopindafenFuhe.getXinChengji());
                } catch (NumberFormatException e) {
                    logger.error("成绩格式错误：{}", zuopindafenFuhe.getXinChengji());
                }
            }
        }
        
        return R.ok();
    }
}
