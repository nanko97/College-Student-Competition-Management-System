package com.dao;

import com.entity.JingsaiSaidaoEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JingsaiSaidaoDao extends BaseMapper<JingsaiSaidaoEntity> {
    List<JingsaiSaidaoEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiSaidaoEntity> wrapper);
}
