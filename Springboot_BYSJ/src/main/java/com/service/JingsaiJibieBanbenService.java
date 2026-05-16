package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiJibieBanbenEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Date;
import java.util.Map;

/**
 * 竞赛级别版本 Service
 */
public interface JingsaiJibieBanbenService extends IService<JingsaiJibieBanbenEntity> {

    /**
     * 分页查询
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据日期查询竞赛级别版本
     */
    JingsaiJibieBanbenEntity getByDate(Long jingsaiId, Date queryDate);

    /**
     * 查询当前有效版本
     */
    JingsaiJibieBanbenEntity getCurrentVersion(Long jingsaiId);

    /**
     * 创建级别版本（自动处理旧版本）
     */
    R saveBanben(JingsaiJibieBanbenEntity banben, String caozuoRen);
}
