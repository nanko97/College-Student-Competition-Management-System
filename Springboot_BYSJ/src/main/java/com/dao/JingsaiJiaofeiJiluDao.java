package com.dao;

import com.entity.JingsaiJiaofeiJiluEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 竞赛缴费记录 DAO
 */
public interface JingsaiJiaofeiJiluDao extends BaseMapper<JingsaiJiaofeiJiluEntity> {

    /**
     * 查询列表视图
     */
    List<JingsaiJiaofeiJiluEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJiaofeiJiluEntity> wrapper);
}
