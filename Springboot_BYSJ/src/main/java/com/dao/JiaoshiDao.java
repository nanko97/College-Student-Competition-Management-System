package com.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.entity.JiaoshiEntity;
import com.entity.view.JiaoshiView;
import com.entity.vo.JiaoshiVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 教师
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface JiaoshiDao extends BaseMapper<JiaoshiEntity> {

    List<JiaoshiVO> selectListVO(@Param("ew") Wrapper<JiaoshiEntity> wrapper);

    JiaoshiVO selectVO(@Param("ew") Wrapper<JiaoshiEntity> wrapper);

    List<JiaoshiView> selectListView(@Param("ew") Wrapper<JiaoshiEntity> wrapper);

    List<JiaoshiView> selectListView(Pagination page, @Param("ew") Wrapper<JiaoshiEntity> wrapper);

    JiaoshiView selectView(@Param("ew") Wrapper<JiaoshiEntity> wrapper);

}
