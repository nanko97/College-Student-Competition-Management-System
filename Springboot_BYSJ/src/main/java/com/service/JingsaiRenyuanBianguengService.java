package com.service;
import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiRenyuanBianguengEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Map;
public interface JingsaiRenyuanBianguengService extends IService<JingsaiRenyuanBianguengEntity> {
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询（带条件）
     */
    PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiRenyuanBianguengEntity> wrapper);

    R applyBiangueng(JingsaiRenyuanBianguengEntity biangueng, String caozuoRen);
    R shenheBiangueng(Long bianguengId, String zhuangtai, String shenheRen);
}
