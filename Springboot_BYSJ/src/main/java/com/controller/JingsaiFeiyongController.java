package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiFeiyongEntity;
import com.service.JingsaiFeiyongService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 竞赛费用配置控制器
 * 功能：配置竞赛报名费用、设置缴费截止日期
 */
@RestController
@RequestMapping("/jingsaiFeiyong")
@Slf4j
public class JingsaiFeiyongController {

    @Autowired
    private JingsaiFeiyongService jingsaiFeiyongService;

    /**
     * 分页查询费用配置列表
     */
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        PageUtils page = jingsaiFeiyongService.queryPage(params);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 查询费用配置列表
     */
    @IgnoreAuth
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = jingsaiFeiyongService.queryPage(params);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 查询费用配置详情
     */
    @IgnoreAuth
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        JingsaiFeiyongEntity feiyong = jingsaiFeiyongService.selectById(id);
        return R.ok().put("jingsaifeiyong", feiyong);
    }

    /**
     * 保存费用配置
     */
    @OperationLog("保存竞赛费用配置")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiFeiyongEntity feiyong, HttpServletRequest request) {
        try {
            // 检查是否已存在该竞赛的费用配置
            JingsaiFeiyongEntity exist = jingsaiFeiyongService.getByJingsaiId(feiyong.getJingsaiId());
            if (exist != null) {
                return R.error("该竞赛已存在费用配置，请使用更新接口");
            }
            
            feiyong.setId(IdWorker.getId());
            jingsaiFeiyongService.insert(feiyong);
            log.info("新增费用配置成功，竞赛ID：{}", feiyong.getJingsaiId());
            return R.ok("费用配置保存成功");
        } catch (Exception e) {
            log.error("保存费用配置异常", e);
            return R.error("保存失败，请重试");
        }
    }

    /**
     * 更新费用配置
     */
    @OperationLog("更新竞赛费用配置")
    @PostMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiFeiyongEntity feiyong) {
        try {
            jingsaiFeiyongService.updateById(feiyong);
            log.info("更新费用配置成功，ID：{}", feiyong.getId());
            return R.ok("费用配置更新成功");
        } catch (Exception e) {
            log.error("更新费用配置异常", e);
            return R.error("更新失败，请重试");
        }
    }

    /**
     * 删除费用配置
     */
    @OperationLog("删除竞赛费用配置")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        try {
            jingsaiFeiyongService.deleteBatchIds(java.util.Arrays.asList(ids));
            log.info("删除费用配置成功，IDs：{}", (Object) ids);
            return R.ok("删除成功");
        } catch (Exception e) {
            log.error("删除费用配置异常", e);
            return R.error("删除失败，请重试");
        }
    }
}
