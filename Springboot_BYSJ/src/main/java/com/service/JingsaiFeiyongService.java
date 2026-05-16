package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiFeiyongEntity;
import com.utils.PageUtils;
import java.util.Map;

/**
 * 竞赛费用配置 Service
 */
public interface JingsaiFeiyongService extends IService<JingsaiFeiyongEntity> {

    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据竞赛ID查询费用配置
     */
    JingsaiFeiyongEntity getByJingsaiId(Long jingsaiId);
}
