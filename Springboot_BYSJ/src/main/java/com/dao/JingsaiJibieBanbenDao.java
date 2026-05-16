package com.dao;

import com.entity.JingsaiJibieBanbenEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 竞赛级别版本 DAO
 */
public interface JingsaiJibieBanbenDao extends BaseMapper<JingsaiJibieBanbenEntity> {

    /**
     * 根据日期查询竞赛级别版本
     */
    JingsaiJibieBanbenEntity getByDate(@Param("jingsaiId") Long jingsaiId, @Param("queryDate") Date queryDate);

    /**
     * 查询当前有效版本
     */
    JingsaiJibieBanbenEntity getCurrentVersion(@Param("jingsaiId") Long jingsaiId);

    /**
     * 查询列表视图
     */
    List<JingsaiJibieBanbenEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJibieBanbenEntity> wrapper);
}
