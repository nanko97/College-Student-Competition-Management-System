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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 竞赛级别版本管理控制器（独立路径）
 * 提供 /jingsai/jibiebanben/* 路径的API接口
 */
@RestController
@RequestMapping("/jingsai/jibiebanben")
@Slf4j
public class JingsaiJibieBanbenController {

    @Autowired
    private JingsaiJibieBanbenService jibieBanbenService;

    /**
     * 分页查询级别版本
     */
    @IgnoreAuth
    @GetMapping("/page")
    public R page(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本列表
     */
    @IgnoreAuth
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = jibieBanbenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 查询级别版本详情
     */
    @IgnoreAuth
    @GetMapping("/info/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().put("jingsaijibiebanben", jibieBanbenService.selectById(id));
    }

    /**
     * 保存级别版本
     */
    @OperationLog("保存竞赛级别版本")
    @PostMapping("/save")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody JingsaiJibieBanbenEntity banben, HttpServletRequest request) {
        String caozuoRen = (String) request.getSession().getAttribute("username");
        return jibieBanbenService.saveBanben(banben, caozuoRen);
    }

    /**
     * 更新级别版本
     */
    @OperationLog("更新竞赛级别版本")
    @PostMapping("/update")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody JingsaiJibieBanbenEntity banben) {
        jibieBanbenService.updateById(banben);
        return R.ok("更新成功");
    }

    /**
     * 删除级别版本
     */
    @OperationLog("删除竞赛级别版本")
    @PostMapping("/delete")
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        jibieBanbenService.deleteBatchIds(java.util.Arrays.asList(ids));
        return R.ok("删除成功");
    }
}
