package com.dao;

import com.entity.JingsaiTuanduiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JingsaiTuanduiDao extends BaseMapper<JingsaiTuanduiEntity> {
    JingsaiTuanduiEntity getByTuanduiBianhao(@Param("tuanduiBianhao") String tuanduiBianhao);
    List<JingsaiTuanduiEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiTuanduiEntity> wrapper);
}
