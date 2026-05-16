package com.dao;

import com.entity.JingsaiJinjiGuanxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 竞赛晋级关系 DAO
 */
public interface JingsaiJinjiGuanxiDao extends BaseMapper<JingsaiJinjiGuanxiEntity> {

    /**
     * 根据父子竞赛ID查询晋级关系
     */
    JingsaiJinjiGuanxiEntity getByFuZiJingsai(@Param("fuJingsaiId") Long fuJingsaiId, @Param("ziJingsaiId") Long ziJingsaiId);

    /**
     * 查询列表视图
     */
    List<JingsaiJinjiGuanxiEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJinjiGuanxiEntity> wrapper);
}
