package com.service;
import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.utils.PageUtils;
import com.utils.R;
import java.util.Map;
public interface JingsaiTuanduiChengyuanService extends IService<JingsaiTuanduiChengyuanEntity> {
    PageUtils queryPage(Map<String, Object> params);
    R addChengyuan(JingsaiTuanduiChengyuanEntity chengyuan);
}
