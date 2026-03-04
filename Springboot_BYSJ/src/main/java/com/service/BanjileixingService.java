package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.BanjileixingEntity;
import com.entity.view.BanjileixingView;
import com.entity.vo.BanjileixingVO;
import com.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 班级类型
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
public interface BanjileixingService extends IService<BanjileixingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<BanjileixingVO> selectListVO(Wrapper<BanjileixingEntity> wrapper);

    BanjileixingVO selectVO(@Param("ew") Wrapper<BanjileixingEntity> wrapper);

    List<BanjileixingView> selectListView(Wrapper<BanjileixingEntity> wrapper);

    BanjileixingView selectView(@Param("ew") Wrapper<BanjileixingEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<BanjileixingEntity> wrapper);

}

