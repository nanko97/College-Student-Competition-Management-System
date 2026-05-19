package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;


import com.dao.ZuopindafenFuheDao;
import com.entity.ZuopindafenEntity;
import com.entity.ZuopindafenFuheEntity;
import com.service.ZuopindafenFuheService;
import com.service.ZuopindafenService;
import com.entity.vo.ZuopindafenFuheVO;
import com.entity.view.ZuopindafenFuheView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service("zuopindafenFuheService")
public class ZuopindafenFuheServiceImpl extends ServiceImpl<ZuopindafenFuheDao, ZuopindafenFuheEntity> implements ZuopindafenFuheService {
	
	@Autowired
	private ZuopindafenService zuopindafenService;
	

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        EntityWrapper<ZuopindafenFuheEntity> wrapper = new EntityWrapper<>();
        
        // 如果传入了学号，添加过滤条件
        if (params.get("xuehao") != null && !params.get("xuehao").toString().isEmpty()) {
            wrapper.eq("xuehao", params.get("xuehao"));
            log.debug("筛选项：学号 = {}", params.get("xuehao"));
        }
        
        // 如果传入了复核状态，添加过滤条件
        if (params.get("fuheStatus") != null && !params.get("fuheStatus").toString().isEmpty()) {
            wrapper.eq("fuhe_status", params.get("fuheStatus").toString());
            log.debug("筛选项：复核状态 = {}", params.get("fuheStatus"));
        }
        
        // 如果传入了竞赛ID列表（教师权限过滤），添加过滤条件
        if (params.get("jingsaiIds") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsaiIds");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                // 先查询这些竞赛的所有作品打分ID
                EntityWrapper<ZuopindafenEntity> zuopinEw = new EntityWrapper<>();
                zuopinEw.in("jingsai_id", jingsaiIds);
                List<ZuopindafenEntity> zuopinList = zuopindafenService.selectList(zuopinEw);
                
                if (zuopinList != null && !zuopinList.isEmpty()) {
                    List<Long> zuopinIds = zuopinList.stream()
                            .map(ZuopindafenEntity::getId)
                            .collect(java.util.stream.Collectors.toList());
                    wrapper.in("zuopindafen_id", zuopinIds);
                    log.debug("筛选项：竞赛ID列表 = {}, 对应的作品打分ID数量 = {}", jingsaiIds, zuopinIds.size());
                } else {
                    // 没有作品打分记录，返回空
                    wrapper.eq("id", -1);
                }
            }
        }
        
        Page<ZuopindafenFuheEntity> page = this.selectPage(
                new Query<ZuopindafenFuheEntity>(params).getPage(),
                wrapper
        );
        log.debug("复核记录查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ZuopindafenFuheEntity> wrapper) {
		  Page<ZuopindafenFuheView> page =new Query<ZuopindafenFuheView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
    @Override
	public List<ZuopindafenFuheVO> selectListVO(Wrapper<ZuopindafenFuheEntity> wrapper) {
 		return baseMapper.selectListVO(wrapper);
	}
	
	@Override
	public ZuopindafenFuheVO selectVO(Wrapper<ZuopindafenFuheEntity> wrapper) {
 		return baseMapper.selectVO(wrapper);
	}
	
	@Override
	public List<ZuopindafenFuheView> selectListView(Wrapper<ZuopindafenFuheEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ZuopindafenFuheView selectView(Wrapper<ZuopindafenFuheEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

}
