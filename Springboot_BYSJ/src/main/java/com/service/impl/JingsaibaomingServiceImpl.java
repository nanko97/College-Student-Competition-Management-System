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
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("jingsaibaomingService")
public class JingsaibaomingServiceImpl extends ServiceImpl<JingsaibaomingDao, JingsaibaomingEntity> implements JingsaibaomingService {


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
        
        // 构建查询条件
        EntityWrapper<JingsaibaomingEntity> wrapper = new EntityWrapper<>();
        
        // 如果有学号参数，过滤该学生的报名记录
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            wrapper.eq("xuehao", params.get("xuehao"));
        }
        
        // 总报名数
        int totalCount = baseMapper.selectCount(wrapper);
        statistics.put("totalBaoming", totalCount);
        
        // 已通过数量
        EntityWrapper<JingsaibaomingEntity> passedWrapper = new EntityWrapper<>();
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            passedWrapper.eq("xuehao", params.get("xuehao"));
        }
        // 数据库中存储的值是"通过"而不是"是"
        passedWrapper.eq("sfsh", "通过");
        int passedCount = baseMapper.selectCount(passedWrapper);
        statistics.put("passedCount", passedCount);
        
        // 待审核数量
        EntityWrapper<JingsaibaomingEntity> pendingWrapper = new EntityWrapper<>();
        if (params.containsKey("xuehao") && params.get("xuehao") != null) {
            pendingWrapper.eq("xuehao", params.get("xuehao"));
        }
        pendingWrapper.eq("sfsh", "待审核");
        int pendingCount = baseMapper.selectCount(pendingWrapper);
        statistics.put("pendingCount", pendingCount);
        
        return statistics;
    }

}
