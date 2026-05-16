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
import com.entity.ZuopindafenFuheEntity;
import com.service.ZuopindafenFuheService;
import com.entity.vo.ZuopindafenFuheVO;
import com.entity.view.ZuopindafenFuheView;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("zuopindafenFuheService")
public class ZuopindafenFuheServiceImpl extends ServiceImpl<ZuopindafenFuheDao, ZuopindafenFuheEntity> implements ZuopindafenFuheService {
	

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
