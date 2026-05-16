package com.dao;

import com.entity.JingsaiJinjiJiluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 竞赛晋级记录 DAO
 */
public interface JingsaiJinjiJiluDao extends BaseMapper<JingsaiJinjiJiluEntity> {

    /**
     * 查询列表视图
     */
    List<JingsaiJinjiJiluEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJinjiJiluEntity> wrapper);
}
