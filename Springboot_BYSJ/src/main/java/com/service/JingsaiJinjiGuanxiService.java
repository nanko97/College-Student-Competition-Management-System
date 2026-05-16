package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiJinjiGuanxiEntity;
import com.utils.PageUtils;
import java.util.Map;

/**
 * 竞赛晋级关系 Service
 */
public interface JingsaiJinjiGuanxiService extends IService<JingsaiJinjiGuanxiEntity> {

    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 分页查询（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJinjiGuanxiEntity> wrapper);

    /**
     * 根据父子竞赛ID查询晋级关系
     */
    JingsaiJinjiGuanxiEntity getByFuZiJingsai(Long fuJingsaiId, Long ziJingsaiId);
}
