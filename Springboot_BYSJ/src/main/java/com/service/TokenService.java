package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.entity.TokenEntity;
import com.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * token
 */
public interface TokenService extends IService<TokenEntity> {
    PageUtils queryPage(Map<String, Object> params);

    List<TokenEntity> selectListView(Wrapper<TokenEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<TokenEntity> wrapper);

    String generateToken(Long userid, String username, String tableName, String role);

    TokenEntity getTokenEntity(String token);

    // ========== 新增方法：Token失效（适配UserController的logout功能） ==========
    void invalidateToken(String token);
}