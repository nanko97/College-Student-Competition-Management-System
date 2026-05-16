package com.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.TuanduiApplyEntity;
import com.utils.PageUtils;
import com.utils.R;

import java.util.Map;

/**
 * 团队申请Service
 */
public interface TuanduiApplyService extends IService<TuanduiApplyEntity> {
    
    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);
    
    /**
     * 分页查询（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, EntityWrapper<TuanduiApplyEntity> ew);
    
    /**
     * 申请加入团队
     */
    R applyJoin(TuanduiApplyEntity apply);
    
    /**
     * 申请退出团队
     */
    R applyQuit(TuanduiApplyEntity apply);
    
    /**
     * 审核团队申请
     */
    R shenheApply(Long applyId, String zhuangtai, String shenheYijian, String shenheXuehao, String shenheXingming);
}
