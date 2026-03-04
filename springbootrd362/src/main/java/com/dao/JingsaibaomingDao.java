package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.JingsaibaomingEntity;
import com.entity.view.JingsaibaomingView;
import com.entity.vo.JingsaibaomingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 竞赛报名
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface JingsaibaomingDao extends BaseMapper<JingsaibaomingEntity> {

    List<JingsaibaomingVO> selectListVO(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    JingsaibaomingVO selectVO(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    List<JingsaibaomingView> selectListView(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    List<JingsaibaomingView> selectListView(Pagination page, @Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    JingsaibaomingView selectView(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

}
