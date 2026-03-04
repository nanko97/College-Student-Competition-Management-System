package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaibaomingEntity;
import com.entity.view.JingsaibaomingView;
import com.entity.vo.JingsaibaomingVO;
import com.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 竞赛报名
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface JingsaibaomingService extends IService<JingsaibaomingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<JingsaibaomingVO> selectListVO(Wrapper<JingsaibaomingEntity> wrapper);

    JingsaibaomingVO selectVO(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    List<JingsaibaomingView> selectListView(Wrapper<JingsaibaomingEntity> wrapper);

    JingsaibaomingView selectView(@Param("ew") Wrapper<JingsaibaomingEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaibaomingEntity> wrapper);

}

