package com.dao;
import com.entity.JingsaiRenyuanBianguengEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface JingsaiRenyuanBianguengDao extends BaseMapper<JingsaiRenyuanBianguengEntity> {
    List<JingsaiRenyuanBianguengEntity> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<JingsaiRenyuanBianguengEntity> wrapper);
}
