package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ZuopindafenFuheEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.ZuopindafenFuheVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.ZuopindafenFuheView;


/**
 * 作品打分复核申请
 *
 * @author 
 * @email 
 * @date 
 */
public interface ZuopindafenFuheService extends IService<ZuopindafenFuheEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ZuopindafenFuheVO> selectListVO(Wrapper<ZuopindafenFuheEntity> wrapper);
   	
   	ZuopindafenFuheVO selectVO(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
   	
   	List<ZuopindafenFuheView> selectListView(Wrapper<ZuopindafenFuheEntity> wrapper);
   	
   	ZuopindafenFuheView selectView(@Param("ew") Wrapper<ZuopindafenFuheEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ZuopindafenFuheEntity> wrapper);
   	
}
