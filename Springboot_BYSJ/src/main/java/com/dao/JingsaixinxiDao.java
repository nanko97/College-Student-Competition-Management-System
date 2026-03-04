package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaixinxiView;
import com.entity.vo.JingsaixinxiVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 竞赛信息
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface JingsaixinxiDao extends BaseMapper<JingsaixinxiEntity> {

    List<JingsaixinxiVO> selectListVO(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    JingsaixinxiVO selectVO(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    List<JingsaixinxiView> selectListView(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    List<JingsaixinxiView> selectListView(Pagination page, @Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    JingsaixinxiView selectView(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

}
