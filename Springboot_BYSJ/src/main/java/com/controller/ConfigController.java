
package com.controller;


import com.annotation.IgnoreAuth;
import com.annotation.OperationLog;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.service.ConfigService;
import com.utils.PageUtils;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 系统配置管理控制器
 * 功能：系统配置的增删改查
 */
@RequestMapping("config")
@RestController
@Slf4j
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ConfigEntity config) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
        PageUtils page = configService.queryPage(params);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ConfigEntity config) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
        PageUtils page = configService.queryPage(params);
        return R.ok().put("data", page).put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id) {
        ConfigEntity config = configService.selectById(id);
        return R.ok().put("data", config);
    }

    /**
     * 详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") String id) {
        ConfigEntity config = configService.selectById(id);
        return R.ok().put("data", config);
    }

    /**
     * 根据name获取信息
     */
    @RequestMapping("/info")
    public R infoByName(@RequestParam String name) {
        ConfigEntity config = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
        return R.ok().put("data", config);
    }

    /**
     * 保存配置
     * 
     * @param config 配置实体
     * @return R 统一返回结果
     */
    @OperationLog("保存系统配置")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public R save(@RequestBody ConfigEntity config) {
        try {
            // 参数校验
            if (!StringUtils.hasText(config.getName())) {
                log.warn("保存配置失败：配置名称为空");
                return R.error("配置名称不能为空");
            }
            
            configService.insert(config);
            log.info("保存配置成功，名称: {}", config.getName());
            return R.ok("保存成功");
            
        } catch (Exception e) {
            log.error("保存配置异常", e);
            return R.error("保存失败，请重试");
        }
    }

    /**
     * 修改配置
     * 
     * @param config 配置实体
     * @return R 统一返回结果
     */
    @OperationLog("修改系统配置")
    @RequestMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R update(@RequestBody ConfigEntity config) {
        try {
            // 参数校验
            if (config.getId() == null || config.getId() <= 0) {
                log.warn("修改配置失败：ID非法");
                return R.error("配置ID非法");
            }
            
            if (!StringUtils.hasText(config.getName())) {
                log.warn("修改配置失败：配置名称为空");
                return R.error("配置名称不能为空");
            }
            
            configService.updateById(config);
            log.info("修改配置成功，ID: {}, 名称: {}", config.getId(), config.getName());
            return R.ok("修改成功");
            
        } catch (Exception e) {
            log.error("修改配置 ID{} 异常", config.getId(), e);
            return R.error("修改失败，请重试");
        }
    }

    /**
     * 删除配置
     * 
     * @param ids 配置ID数组
     * @return R 统一返回结果
     */
    @OperationLog("删除系统配置")
    @RequestMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public R delete(@RequestBody Long[] ids) {
        try {
            // 参数校验
            if (ids == null || ids.length == 0) {
                log.warn("删除配置失败：ID数组为空");
                return R.error("请选择要删除的配置");
            }
            
            configService.deleteBatchIds(Arrays.asList(ids));
            log.info("删除配置成功，IDs: {}", Arrays.toString(ids));
            return R.ok("删除成功");
            
        } catch (Exception e) {
            log.error("删除配置 IDs{} 异常", Arrays.toString(ids), e);
            return R.error("删除失败，请重试");
        }
    }
}
