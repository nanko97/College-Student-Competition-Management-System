package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.ZuopindafenDao;
import com.entity.ZuopindafenEntity;
import com.entity.view.ZuopindafenView;
import com.entity.vo.ZuopindafenVO;
import com.service.ZuopindafenService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("zuopindafenService")
@Slf4j
public class ZuopindafenServiceImpl extends ServiceImpl<ZuopindafenDao, ZuopindafenEntity> implements ZuopindafenService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<ZuopindafenEntity> ew = new EntityWrapper<>();
        
        // 竞赛名称模糊查询
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.debug("筛选项：竞赛名称 = {}", params.get("jingsaimingcheng"));
        }
        
        // 学生姓名模糊查询
        if (params.get("xueshengxingming") != null && !params.get("xueshengxingming").toString().isEmpty()) {
            ew.like("xueshengxingming", params.get("xueshengxingming").toString());
            log.debug("筛选项：学生姓名 = {}", params.get("xueshengxingming"));
        }
        
        Page<ZuopindafenEntity> page = this.selectPage(
                new Query<ZuopindafenEntity>(params).getPage(),
                ew
        );
        log.debug("作品打分查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ZuopindafenEntity> wrapper) {
        Page<ZuopindafenView> page = new Query<ZuopindafenView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ZuopindafenVO> selectListVO(Wrapper<ZuopindafenEntity> wrapper) {
        return baseMapper.selectListVO(wrapper);
    }

    @Override
    public ZuopindafenVO selectVO(Wrapper<ZuopindafenEntity> wrapper) {
        return baseMapper.selectVO(wrapper);
    }

    @Override
    public List<ZuopindafenView> selectListView(Wrapper<ZuopindafenEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ZuopindafenView selectView(Wrapper<ZuopindafenEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }

}
