package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.TuanduiApplyEntity;
import com.service.TuanduiApplyService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 团队申请管理Controller
 */
@RestController
@RequestMapping("/tuandui/apply")
@Slf4j
public class TuanduiApplyController {

    @Autowired
    private TuanduiApplyService tuanduiApplyService;

    /**
     * 分页查询申请列表
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        PageUtils page = tuanduiApplyService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询申请详情
     */
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        TuanduiApplyEntity apply = tuanduiApplyService.selectById(id);
        return R.ok().put("data", apply);
    }

    /**
     * 申请加入团队
     */
    @PostMapping("/join")
    public R applyJoin(@RequestBody TuanduiApplyEntity apply) {
        log.info("申请加入团队 - 学号: {}, 团队ID: {}", apply.getXuehao(), apply.getTuanduiId());
        return tuanduiApplyService.applyJoin(apply);
    }

    /**
     * 申请退出团队
     */
    @PostMapping("/quit")
    public R applyQuit(@RequestBody TuanduiApplyEntity apply) {
        log.info("申请退出团队 - 学号: {}, 团队ID: {}", apply.getXuehao(), apply.getTuanduiId());
        return tuanduiApplyService.applyQuit(apply);
    }

    /**
     * 审核团队申请（负责人或教师）
     */
    @PostMapping("/shenhe")
    public R shenhe(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long applyId = Long.parseLong(params.get("applyId").toString());
            String zhuangtai = params.get("zhuangtai").toString(); // 已通过/已驳回
            String shenheYijian = params.get("shenheYijian") != null ? params.get("shenheYijian").toString() : "";
            
            // 获取审核人信息
            String shenheXuehao = (String) request.getSession().getAttribute("username");
            String tableName = (String) request.getSession().getAttribute("tableName");
            
            // 如果是教师，使用工号；如果是学生（负责人），使用学号
            String shenheXingming = shenheXuehao;
            if ("jiaoshi".equals(tableName)) {
                // 这里可以从教师表获取姓名，暂时用工号
                shenheXingming = "教师-" + shenheXuehao;
            }

            log.info("审核团队申请 - ID: {}, 结果: {}, 审核人: {}", applyId, zhuangtai, shenheXuehao);
            return tuanduiApplyService.shenheApply(applyId, zhuangtai, shenheYijian, shenheXuehao, shenheXingming);

        } catch (Exception e) {
            log.error("审核团队申请异常", e);
            return R.error("审核失败：" + e.getMessage());
        }
    }

    /**
     * 查询我的申请（学生端）
     */
    @GetMapping("/my/list")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String xuehao = (String) request.getSession().getAttribute("username");
        
        EntityWrapper<TuanduiApplyEntity> ew = new EntityWrapper<>();
        ew.eq("xuehao", xuehao);
        
        // 可以按状态筛选
        if (params.get("applyStatus") != null && !params.get("applyStatus").toString().isEmpty()) {
            ew.eq("apply_status", params.get("applyStatus").toString());
        }
        
        // 按申请时间倒序
        ew.orderBy("apply_time", false);
        
        PageUtils page = tuanduiApplyService.queryPage(params, ew);
        return R.ok().put("page", page);
    }

    /**
     * 查询待我审核的申请（负责人端）
     */
    @GetMapping("/shenhe/list")
    public R shenheList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        String fuzerenXuehao = (String) request.getSession().getAttribute("username");
        
        // 查询该学生作为负责人的所有团队
        // TODO: 需要从团队表查询负责人为该学生的团队ID列表
        
        // 暂时查询所有待审核的申请
        EntityWrapper<TuanduiApplyEntity> ew = new EntityWrapper<>();
        ew.eq("apply_status", "待审核");
        ew.orderBy("apply_time", false);
        
        PageUtils page = tuanduiApplyService.queryPage(params, ew);
        return R.ok().put("page", page);
    }

    /**
     * 删除申请记录
     */
    @OperationLog("删除团队申请记录")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        tuanduiApplyService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
    }
}
