package com.dao;

import com.entity.JingsaiFeiyongEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 竞赛费用配置 DAO
 */
public interface JingsaiFeiyongDao extends BaseMapper<JingsaiFeiyongEntity> {

    /**
     * 根据竞赛ID查询费用配置
     */
    JingsaiFeiyongEntity getByJingsaiId(@Param("jingsaiId") Long jingsaiId);

    /**
     * 查询列表视图
     */
    List<JingsaiFeiyongEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiFeiyongEntity> wrapper);
}
