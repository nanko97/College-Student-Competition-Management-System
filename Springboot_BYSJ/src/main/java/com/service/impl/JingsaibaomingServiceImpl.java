package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaibaomingDao;
import com.entity.JingsaibaomingEntity;
import com.entity.view.JingsaibaomingView;
import com.entity.vo.JingsaibaomingVO;
import com.service.JingsaibaomingService;
import com.utils.PageUtils;
import com.utils.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("jingsaibaomingService")
public class JingsaibaomingServiceImpl extends ServiceImpl<JingsaibaomingDao, JingsaibaomingEntity> implements JingsaibaomingService {

    private static final Logger log = LoggerFactory.getLogger(JingsaibaomingServiceImpl.class);


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JingsaibaomingEntity> page = this.selectPage(
                new Query<JingsaibaomingEntity>(params).getPage(),
                new EntityWrapper<JingsaibaomingEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaibaomingEntity> wrapper) {
        Page<JingsaibaomingView> page = new Query<JingsaibaomingView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<JingsaibaomingVO> selectListVO(Wrapper<JingsaibaomingEntity> wrapper) {
        return baseMapper.selectListVO(wrapper);
    }

    @Override
    public JingsaibaomingVO selectVO(Wrapper<JingsaibaomingEntity> wrapper) {
        return baseMapper.selectVO(wrapper);
    }

    @Override
    public List<JingsaibaomingView> selectListView(Wrapper<JingsaibaomingEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public JingsaibaomingView selectView(Wrapper<JingsaibaomingEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 教师没有创建任何竞赛时，直接返回空统计
        if (params.containsKey("jingsai_id") && "-1".equals(params.get("jingsai_id").toString())) {
            log.info("教师没有创建任何竞赛，直接返回空统计");
            statistics.put("totalBaoming", 0);
            statistics.put("passedCount", 0);
            statistics.put("pendingCount", 0);
            return statistics;
        }
        
        // 构建查询条件
        EntityWrapper<JingsaibaomingEntity> wrapper = new EntityWrapper<>();
        
        // 教师权限过滤：只统计特定竞赛ID列表的报名数据
        if (params.containsKey("jingsai_id_in") && params.get("jingsai_id_in") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                wrapper.in("jingsai_id", jingsaiIds);
                log.debug("教师权限过滤 - 统计竞赛ID列表的报名数据: {}", jingsaiIds);
            }
        }
        
        // 如果有学号参数，过滤该学生的报名记录
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            wrapper.eq("xuehao", params.get("xuehao"));
        }
        
        // 总报名数
        int totalCount = baseMapper.selectCount(wrapper);
        statistics.put("totalBaoming", totalCount);
        
        // 已通过数量
        EntityWrapper<JingsaibaomingEntity> passedWrapper = new EntityWrapper<>();
        // 教师权限过滤
        if (params.containsKey("jingsai_id_in") && params.get("jingsai_id_in") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                passedWrapper.in("jingsai_id", jingsaiIds);
            }
        }
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            passedWrapper.eq("xuehao", params.get("xuehao"));
        }
        // 数据库中存储的值是"是"或"通过"都表示审核通过
        passedWrapper.andNew("sfsh = '是' OR sfsh = '通过'");
        int passedCount = baseMapper.selectCount(passedWrapper);
        statistics.put("passedCount", passedCount);
        
        // 待审核数量
        EntityWrapper<JingsaibaomingEntity> pendingWrapper = new EntityWrapper<>();
        // 教师权限过滤
        if (params.containsKey("jingsai_id_in") && params.get("jingsai_id_in") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                pendingWrapper.in("jingsai_id", jingsaiIds);
            }
        }
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            pendingWrapper.eq("xuehao", params.get("xuehao"));
        }
        // 待审核：sfsh为NULL、空字符串或"待审核"
        pendingWrapper.andNew("sfsh IS NULL OR sfsh = '' OR sfsh = '待审核'");
        int pendingCount = baseMapper.selectCount(pendingWrapper);
        statistics.put("pendingCount", pendingCount);
        
        return statistics;
    }

}
