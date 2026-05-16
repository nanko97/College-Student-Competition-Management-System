package com.service;
import com.baomidou.mybatisplus.service.IService;
import com.entity.JingsaiSaidaoEntity;
import com.utils.PageUtils;
import java.util.Map;
public interface JingsaiSaidaoService extends IService<JingsaiSaidaoEntity> {
    PageUtils queryPage(Map<String, Object> params);
}
