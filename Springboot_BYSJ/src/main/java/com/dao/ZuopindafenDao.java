package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.ZuopindafenEntity;
import com.entity.view.ZuopindafenView;
import com.entity.vo.ZuopindafenVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 作品打分
 *
 */
public interface ZuopindafenDao extends BaseMapper<ZuopindafenEntity> {

    List<ZuopindafenVO> selectListVO(@Param("ew") Wrapper<ZuopindafenEntity> wrapper);

    ZuopindafenVO selectVO(@Param("ew") Wrapper<ZuopindafenEntity> wrapper);

    List<ZuopindafenView> selectListView(@Param("ew") Wrapper<ZuopindafenEntity> wrapper);

    List<ZuopindafenView> selectListView(Pagination page, @Param("ew") Wrapper<ZuopindafenEntity> wrapper);

    ZuopindafenView selectView(@Param("ew") Wrapper<ZuopindafenEntity> wrapper);

}
