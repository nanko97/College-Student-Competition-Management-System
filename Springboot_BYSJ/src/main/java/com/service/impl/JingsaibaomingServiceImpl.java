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

}
