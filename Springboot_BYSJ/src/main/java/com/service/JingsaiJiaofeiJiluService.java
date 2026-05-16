package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiJiaofeiJiluEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Map;

/**
 * 竞赛缴费记录 Service
 */
public interface JingsaiJiaofeiJiluService extends IService<JingsaiJiaofeiJiluEntity> {

    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJiaofeiJiluEntity> wrapper);

    /**
     * 审核缴费
     */
    R shenheJiaofei(Long jiaofeiId, String zhuangtai, String yijian, String shenheRen);
}
