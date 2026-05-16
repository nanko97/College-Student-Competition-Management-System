package com.dao;

import com.entity.JingsaiTuanduiChengyuanEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JingsaiTuanduiChengyuanDao extends BaseMapper<JingsaiTuanduiChengyuanEntity> {
    List<JingsaiTuanduiChengyuanEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiTuanduiChengyuanEntity> wrapper);
}
