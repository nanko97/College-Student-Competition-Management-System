package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiJinjiJiluEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Map;

/**
 * 竞赛晋级记录 Service
 */
public interface JingsaiJinjiJiluService extends IService<JingsaiJinjiJiluEntity> {

    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJinjiJiluEntity> wrapper);

    /**
     * 发起晋级
     */
    R initiateJinji(Long baomingId, Long xinJingsaiId, String jinjiYuanyin, String caozuoRen);

    /**
     * 批量发起晋级
     */
    R batchInitiateJinji(java.util.List<Long> baomingIds, Long xinJingsaiId, String jinjiYuanyin, String caozuoRen);

    /**
     * 审核晋级
     */
    R shenheJinji(Long jinjiId, String zhuangtai, String shenheRen);
}
