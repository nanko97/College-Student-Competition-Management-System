package com.dao;

import com.entity.ZuopindafenFuheEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.vo.ZuopindafenFuheVO;
import com.entity.view.ZuopindafenFuheView;


/**
 * 作品打分复核申请
 * 
 * @author 
 * @email 
 * @date 
 */
public interface ZuopindafenFuheDao extends BaseMapper<ZuopindafenFuheEntity> {
	
	List<ZuopindafenFuheVO> selectListVO(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
	
	ZuopindafenFuheVO selectVO(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
	
	List<ZuopindafenFuheView> selectListView(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);

	List<ZuopindafenFuheView> selectListView(Pagination page,@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
	
	ZuopindafenFuheView selectView(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
	
}
