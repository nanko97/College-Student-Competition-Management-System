package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaixinxiView;
import com.entity.vo.JingsaixinxiVO;
import com.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 竞赛信息
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface JingsaixinxiService extends IService<JingsaixinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<JingsaixinxiVO> selectListVO(Wrapper<JingsaixinxiEntity> wrapper);

    JingsaixinxiVO selectVO(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    List<JingsaixinxiView> selectListView(Wrapper<JingsaixinxiEntity> wrapper);

    JingsaixinxiView selectView(@Param("ew") Wrapper<JingsaixinxiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaixinxiEntity> wrapper);

}

