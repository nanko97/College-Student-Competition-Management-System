package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.service.CommonService;
import com.service.ConfigService;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 通用接口
 */
@Slf4j
@RestController
public class CommonController {
    @Autowired
    private CommonService commonService;

    @Autowired
    private ConfigService configService;
    
    // 允许的表名白名单（防止 SQL 注入）
    private static final Set<String> ALLOWED_TABLES = new HashSet<>(Arrays.asList(
        "xuesheng", "jiaoshi", "jingsaixinxi", "jingsaibaoming", 
        "zuopindafen", "config", "token", "users"
    ));
    
    // 允许的列名白名单
    private static final Pattern COLUMN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    
    /**
     * 校验表名是否合法
     */
    private boolean isValidTable(String tableName) {
        return tableName != null && ALLOWED_TABLES.contains(tableName.toLowerCase());
    }
    
    /**
     * 校验列名是否合法
     */
    private boolean isValidColumn(String columnName) {
        return columnName != null && COLUMN_PATTERN.matcher(columnName).matches();
    }

    /**
     * 获取table表中的column列表(联动接口)
     *
     * @param table
     * @param column
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/option/{tableName}/{columnName}")
    public R getOption(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, String level, String parent) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(columnName)) {
            log.warn("非法的列名：{}", columnName);
            return R.error("非法的列名");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("table", tableName);
        params.put("column", columnName);
        if (StringUtils.isNotBlank(level)) {
            params.put("level", level);
        }
        if (StringUtils.isNotBlank(parent)) {
            params.put("parent", parent);
        }
        List<String> data = commonService.getOption(params);
        return R.ok().put("data", data);
    }

    /**
     * 根据table中的column获取单条记录
     *
     * @param table
     * @param column
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/follow/{tableName}/{columnName}")
    public R getFollowByOption(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName, @RequestParam String columnValue) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(columnName)) {
            log.warn("非法的列名：{}", columnName);
            return R.error("非法的列名");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("table", tableName);
        params.put("column", columnName);
        params.put("columnValue", columnValue);
        Map<String, Object> result = commonService.getFollowByOption(params);
        return R.ok().put("data", result);
    }

    /**
     * 修改table表的sfsh状态
     *
     * @param table
     * @param map
     * @return
     */
    @RequestMapping("/sh/{tableName}")
    public R sh(@PathVariable("tableName") String tableName, @RequestBody Map<String, Object> map) {
        // 校验表名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        map.put("table", tableName);
        commonService.sh(map);
        return R.ok();
    }

    /**
     * 获取需要提醒的记录数
     *
     * @param tableName
     * @param columnName
     * @param type       1:数字 2:日期
     * @param map
     * @return
     */
    @IgnoreAuth
    @RequestMapping("/remind/{tableName}/{columnName}/{type}")
    public R remindCount(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName,
                         @PathVariable("type") String type, @RequestParam Map<String, Object> map) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(columnName)) {
            log.warn("非法的列名：{}", columnName);
            return R.error("非法的列名");
        }
        
        map.put("table", tableName);
        map.put("column", columnName);
        map.put("type", type);

        if (type.equals("2")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            Date remindStartDate = null;
            Date remindEndDate = null;
            if (map.get("remindstart") != null) {
                Integer remindStart = Integer.parseInt(map.get("remindstart").toString());
                c.setTime(new Date());
                c.add(Calendar.DAY_OF_MONTH, remindStart);
                remindStartDate = c.getTime();
                map.put("remindstart", sdf.format(remindStartDate));
            }
            if (map.get("remindend") != null) {
                Integer remindEnd = Integer.parseInt(map.get("remindend").toString());
                c.setTime(new Date());
                c.add(Calendar.DAY_OF_MONTH, remindEnd);
                remindEndDate = c.getTime();
                map.put("remindend", sdf.format(remindEndDate));
            }
        }

        int count = commonService.remindCount(map);
        return R.ok().put("count", count);
    }

    /**
     * 单列求和
     */
    @IgnoreAuth
    @RequestMapping("/cal/{tableName}/{columnName}")
    public R cal(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(columnName)) {
            log.warn("非法的列名：{}", columnName);
            return R.error("非法的列名");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("table", tableName);
        params.put("column", columnName);
        Map<String, Object> result = commonService.selectCal(params);
        return R.ok().put("data", result);
    }

    /**
     * 分组统计
     */
    @IgnoreAuth
    @RequestMapping("/group/{tableName}/{columnName}")
    public R group(@PathVariable("tableName") String tableName, @PathVariable("columnName") String columnName) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(columnName)) {
            log.warn("非法的列名：{}", columnName);
            return R.error("非法的列名");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("table", tableName);
        params.put("column", columnName);
        List<Map<String, Object>> result = commonService.selectGroup(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> m : result) {
            for (String k : m.keySet()) {
                if (m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date) m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

    /**
     * （按值统计）
     */
    @IgnoreAuth
    @RequestMapping("/value/{tableName}/{xColumnName}/{yColumnName}")
    public R value(@PathVariable("tableName") String tableName, @PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName) {
        // 校验表名和列名，防止 SQL 注入
        if (!isValidTable(tableName)) {
            log.warn("非法的表名：{}", tableName);
            return R.error("非法的表名");
        }
        if (!isValidColumn(xColumnName)) {
            log.warn("非法的 X 列名：{}", xColumnName);
            return R.error("非法的列名");
        }
        if (!isValidColumn(yColumnName)) {
            log.warn("非法的 Y 列名：{}", yColumnName);
            return R.error("非法的列名");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("table", tableName);
        params.put("xColumn", xColumnName);
        params.put("yColumn", yColumnName);
        List<Map<String, Object>> result = commonService.selectValue(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String, Object> m : result) {
            for (String k : m.keySet()) {
                if (m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date) m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

}
