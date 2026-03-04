package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.TokenDao;
import com.entity.TokenEntity;
import com.service.TokenService;
import com.utils.CommonUtil;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j; // 新增日志注解
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * token
 */
@Service("tokenService")
@Slf4j // 新增：日志注解，便于排查token失效相关问题
public class TokenServiceImpl extends ServiceImpl<TokenDao, TokenEntity> implements TokenService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TokenEntity> page = this.selectPage(
                new Query<TokenEntity>(params).getPage(),
                new EntityWrapper<TokenEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<TokenEntity> selectListView(Wrapper<TokenEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params,
                               Wrapper<TokenEntity> wrapper) {
        Page<TokenEntity> page = new Query<TokenEntity>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public String generateToken(Long userid, String username, String tableName, String role) {
        TokenEntity tokenEntity = this.selectOne(new EntityWrapper<TokenEntity>().eq("userid", userid).eq("role", role));
        String token = CommonUtil.getRandomString(32);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        if (tokenEntity != null) {
            tokenEntity.setToken(token);
            tokenEntity.setExpiratedtime(cal.getTime());
            this.updateById(tokenEntity);
        } else {
            this.insert(new TokenEntity(userid, username, tableName, role, token, cal.getTime()));
        }
        return token;
    }

    @Override
    public TokenEntity getTokenEntity(String token) {
        TokenEntity tokenEntity = this.selectOne(new EntityWrapper<TokenEntity>().eq("token", token));
        if (tokenEntity == null || tokenEntity.getExpiratedtime().getTime() < new Date().getTime()) {
            return null;
        }
        return tokenEntity;
    }

    // ========== 新增：Token失效方法（适配UserController的logout功能） ==========
    @Override
    public void invalidateToken(String token) {
        // 1. 参数校验：避免空指针
        if (token == null || token.trim().isEmpty()) {
            log.warn("Token失效失败：传入的token为空");
            return;
        }

        // 2. 查询token记录
        TokenEntity tokenEntity = this.getTokenEntity(token);
        if (tokenEntity == null) {
            log.warn("Token失效失败：token[{}]不存在或已过期", token.substring(0, 10) + "****");
            return;
        }

        // 3. 核心逻辑：将token过期时间改为当前时间（立即失效，保留记录，兼容原有校验逻辑）
        tokenEntity.setExpiratedtime(new Date()); // 过期时间设为现在，getTokenEntity会判定为无效
        this.updateById(tokenEntity);

        log.info("Token失效成功：token[{}]，关联用户ID：{}，角色：{}",
                token.substring(0, 10) + "****", tokenEntity.getUserid(), tokenEntity.getRole());
    }
}