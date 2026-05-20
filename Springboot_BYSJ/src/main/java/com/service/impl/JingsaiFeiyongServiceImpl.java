package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiFeiyongDao;
import com.entity.JingsaiFeiyongEntity;
import com.service.JingsaiFeiyongService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service("jingsaiFeiyongService")
@Slf4j
public class JingsaiFeiyongServiceImpl extends ServiceImpl<JingsaiFeiyongDao, JingsaiFeiyongEntity> implements JingsaiFeiyongService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiFeiyongEntity> ew = new EntityWrapper<>();
        
        // 教师权限过滤：只查询特定竞赛ID列表的费用配置
        if (params.get("jingsai_id_in") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                ew.in("jingsai_id", jingsaiIds);
                log.debug("教师权限过滤 - 竞赛ID列表: {}", jingsaiIds);
            }
        }
        
        // 竞赛名称模糊查询
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.debug("筛选项：竞赛名称 = {}", params.get("jingsaimingcheng"));
        }
        
        Page<JingsaiFeiyongEntity> page = this.selectPage(
            new Query<JingsaiFeiyongEntity>(params).getPage(),
            ew
        );
        log.debug("费用配置查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public JingsaiFeiyongEntity getByJingsaiId(Long jingsaiId) {
        return baseMapper.getByJingsaiId(jingsaiId);
    }
}
