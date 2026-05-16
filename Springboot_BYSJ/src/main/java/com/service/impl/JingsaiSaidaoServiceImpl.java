package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiSaidaoDao;
import com.entity.JingsaiSaidaoEntity;
import com.service.JingsaiSaidaoService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service("jingsaiSaidaoService")
@Slf4j
public class JingsaiSaidaoServiceImpl extends ServiceImpl<JingsaiSaidaoDao, JingsaiSaidaoEntity> implements JingsaiSaidaoService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiSaidaoEntity> ew = new EntityWrapper<>();
        
        // 竞赛名称模糊查询
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.debug("筛选项：竞赛名称 = {}", params.get("jingsaimingcheng"));
        }
        
        Page<JingsaiSaidaoEntity> page = this.selectPage(new Query<JingsaiSaidaoEntity>(params).getPage(), ew);
        log.debug("赛道配置查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }
}
