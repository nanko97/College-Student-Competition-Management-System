package com.controller;

import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.JingsaiJibieBanbenEntity;
import com.service.JingsaiJibieBanbenService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 竞赛级别动态认定控制器
 * 功能：管理竞赛级别版本、根据日期查询级别、历史追溯
 */
@RestController
@RequestMapping("/jingsai/jibie")
@Slf4j
public class JingsaiJibieController {

    @Autowired
    private JingsaiJibieBanbenService jibieBanbenService;

    /**
     * 创建级别版本
     */
    @OperationLog("创建竞赛级别版本")
    @PostMapping("/banben/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R saveBanben(@RequestBody JingsaiJibieBanbenEntity banben, HttpServletRequest request) {
        try {
            String caozuoRen = (String) request.getSession().getAttribute("username");
            R result = jibieBanbenService.saveBanben(banben, caozuoRen);
            return result;
        } catch (Exception e) {
            log.error("创建级别版本异常", e);
            return R.error("操作失败，请重试");
        }
    }

    /**
     * 更新级别版本
     */
    @OperationLog("更新竞赛级别版本")
    @PostMapping("/banben/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R updateBanben(@RequestBody JingsaiJibieBanbenEntity banben) {
        try {
            jibieBanbenService.updateById(banben);
            return R.ok("更新成功");
        } catch (Exception e) {
            log.error("更新级别版本异常", e);
            return R.error("更新失败，请重试");
        }
    }

    /**
     * 查询级别版本列表
     */
    @IgnoreAuth
    @GetMapping("/banben/list")
    public R listBanben(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询当前有效级别
     */
    @IgnoreAuth
    @GetMapping("/banben/current/{jingsaiId}")
    public R getCurrentVersion(@PathVariable Long jingsaiId) {
        JingsaiJibieBanbenEntity banben = jibieBanbenService.getCurrentVersion(jingsaiId);
        if (banben == null) {
            return R.ok().put("data", null);
        }
        return R.ok().put("data", banben);
    }

    /**
     * 查询历史级别版本
     */
    @IgnoreAuth
    @GetMapping("/banben/history/{jingsaiId}")
    public R getHistoryVersions(@PathVariable Long jingsaiId) {
        EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
        ew.eq("jingsai_id", jingsaiId);
        ew.orderBy("shengxiao_riqi", false); // 按生效日期降序
        List<JingsaiJibieBanbenEntity> list = jibieBanbenService.selectList(ew);
        return R.ok().put("data", list);
    }

    /**
     * 根据日期查询级别
     */
    @IgnoreAuth
    @GetMapping("/queryByDate")
    public R queryJibieByDate(@RequestParam Long jingsaiId,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date queryDate) {
        JingsaiJibieBanbenEntity banben = jibieBanbenService.getByDate(jingsaiId, queryDate);
        if (banben == null) {
            return R.ok().put("data", null).put("msg", "该日期无有效级别认定");
        }
        return R.ok().put("data", banben);
    }

    // ==================== 兼容前端路径 /jingsai/jibiebanben/* ====================

    /**
     * 新增级别版本（兼容前端路径）
     */
    @OperationLog("创建竞赛级别版本")
    @PostMapping("/jibiebanben/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R saveBanbenCompat(@RequestBody JingsaiJibieBanbenEntity banben, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        return jibieBanbenService.saveBanben(banben, caozuoRen);
    }

    /**
     * 更新级别版本（兼容前端路径）
     */
    @OperationLog("更新竞赛级别版本")
    @PostMapping("/jibiebanben/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R updateBanbenCompat(@RequestBody JingsaiJibieBanbenEntity banben) {
        jibieBanbenService.updateById(banben);
        return R.ok("更新成功");
    }

    /**
     * 删除级别版本（兼容前端路径）
     */
    @OperationLog("删除竞赛级别版本")
    @PostMapping("/jibiebanben/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R deleteBanbenCompat(@RequestBody Long[] ids) {
        jibieBanbenService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
    }

    /**
     * 分页查询级别版本（兼容前端路径）
     */
    @IgnoreAuth
    @GetMapping("/jibiebanben/page")
    public R pageBanbenCompat(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本详情（兼容前端路径）
     */
    @IgnoreAuth
    @GetMapping("/jibiebanben/info/{id}")
    public R infoBanbenCompat(@PathVariable Long id) {
        return R.ok().put("jingsaijibiebanben", jibieBanbenService.selectById(id));
    }

    /**
     * 查询级别版本列表（兼容前端路径）
     */
    @IgnoreAuth
    @GetMapping("/jibiebanben/list")
    public R listBanbenCompat(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }
}
