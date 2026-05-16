package com.service;
import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.JingsaiTuanduiEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Map;
public interface JingsaiTuanduiService extends IService<JingsaiTuanduiEntity> {
    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaiTuanduiEntity> wrapper);
    JingsaiTuanduiEntity getByTuanduiBianhao(String tuanduiBianhao);
    R createTuandui(JingsaiTuanduiEntity tuandui, String caozuoRen);
}
