package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiJinjiGuanxiDao;
import com.entity.JingsaiJinjiGuanxiEntity;
import com.service.JingsaiJinjiGuanxiService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service("jingsaiJinjiGuanxiService")
@Slf4j
public class JingsaiJinjiGuanxiServiceImpl extends ServiceImpl<JingsaiJinjiGuanxiDao, JingsaiJinjiGuanxiEntity> implements JingsaiJinjiGuanxiService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiJinjiGuanxiEntity> ew = new EntityWrapper<>();
        
        // 父竞赛ID精确匹配
        if (params.get("fuJingsaiId") != null && !params.get("fuJingsaiId").toString().isEmpty()) {
            ew.eq("fu_jingsai_id", params.get("fuJingsaiId").toString());
            log.debug("筛选项：父竞赛ID = {}", params.get("fuJingsaiId"));
        }
        
        Page<JingsaiJinjiGuanxiEntity> page = this.selectPage(
            new Query<JingsaiJinjiGuanxiEntity>(params).getPage(),
            ew
        );
        log.debug("晋级关系查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }
    
    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaiJinjiGuanxiEntity> wrapper) {
        Page<JingsaiJinjiGuanxiEntity> page = this.selectPage(
            new Query<JingsaiJinjiGuanxiEntity>(params).getPage(),
            wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public JingsaiJinjiGuanxiEntity getByFuZiJingsai(Long fuJingsaiId, Long ziJingsaiId) {
        return baseMapper.getByFuZiJingsai(fuJingsaiId, ziJingsaiId);
    }
}
