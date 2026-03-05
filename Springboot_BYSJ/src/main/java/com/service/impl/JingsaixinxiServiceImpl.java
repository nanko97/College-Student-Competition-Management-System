package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaixinxiDao;
import com.entity.JingsaixinxiEntity;
import com.entity.view.JingsaixinxiView;
import com.entity.vo.JingsaixinxiVO;
import com.service.CacheService;
import com.service.JingsaixinxiService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 竞赛信息服务实现类
 * 【性能优化版】集成 Redis 缓存，提升查询性能
 * 
 * @author 毕业设计优化版
 * @date 2026-03-05
 */
@Service("jingsaixinxiService")
@Slf4j
public class JingsaixinxiServiceImpl extends ServiceImpl<JingsaixinxiDao, JingsaixinxiEntity> implements JingsaixinxiService {
    
    @Autowired(required = false)
    private CacheService cacheService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JingsaixinxiEntity> page = this.selectPage(
                new Query<JingsaixinxiEntity>(params).getPage(),
                new EntityWrapper<JingsaixinxiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaixinxiEntity> wrapper) {
        Page<JingsaixinxiView> page = new Query<JingsaixinxiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<JingsaixinxiVO> selectListVO(Wrapper<JingsaixinxiEntity> wrapper) {
        return baseMapper.selectListVO(wrapper);
    }

    @Override
    public JingsaixinxiVO selectVO(Wrapper<JingsaixinxiEntity> wrapper) {
        return baseMapper.selectVO(wrapper);
    }

    @Override
    public List<JingsaixinxiView> selectListView(Wrapper<JingsaixinxiEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public JingsaixinxiView selectView(Wrapper<JingsaixinxiEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }
    
    /**
     * 查询竞赛详情（带缓存）
     * 【性能优化】先查缓存，缓存未命中再查数据库
     * 
     * @param id 竞赛 ID
     * @return 竞赛信息实体
     */
    public JingsaixinxiEntity selectByIdWithCache(Long id) {
        if (id == null) {
            return null;
        }
        
        // 1. 尝试从缓存获取
        if (cacheService != null) {
            Object cached = cacheService.getCachedJingsai(id);
            if (cached != null) {
                log.debug("缓存命中：id={}", id);
                return (JingsaixinxiEntity) cached;
            }
        }
        
        // 2. 缓存未命中，从数据库查询
        JingsaixinxiEntity entity = this.selectById(id);
        
        // 3. 写入缓存
        if (entity != null && cacheService != null) {
            cacheService.cacheJingsai(id, entity);
            log.debug("写入缓存：id={}", id);
        }
        
        return entity;
    }
    
    /**
     * 删除竞赛缓存
     * 【性能优化】数据更新时清除缓存，保证数据一致性
     * 
     * @param id 竞赛 ID
     */
    public void deleteJingsaiCache(Long id) {
        if (cacheService != null && id != null) {
            cacheService.deleteJingsaiCache(id);
            log.debug("删除缓存：id={}", id);
        }
    }
}
