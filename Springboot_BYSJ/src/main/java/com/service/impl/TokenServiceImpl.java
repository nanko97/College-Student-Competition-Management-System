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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * token
 */
@Service("tokenService")
@Slf4j
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

    @Override
    public void invalidateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Token 失效失败：传入的 token 为空");
            return;
        }

        TokenEntity tokenEntity = this.getTokenEntity(token);
        if (tokenEntity == null) {
            log.warn("Token 失效失败：token[{}] 不存在或已过期", token.substring(0, 10) + "****");
            return;
        }

        tokenEntity.setExpiratedtime(new Date());
        this.updateById(tokenEntity);

        log.info("Token 失效成功：token[{}]，关联用户 ID：{}，角色：{}",
                token.substring(0, 10) + "****", tokenEntity.getUserid(), tokenEntity.getRole());
    }

    @Override
    public int cleanExpiredTokens() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        List<TokenEntity> expiredTokens = this.selectList(
            new EntityWrapper<TokenEntity>().lt("expiratedtime", cal.getTime())
        );
        
        if (!expiredTokens.isEmpty()) {
            for (TokenEntity token : expiredTokens) {
                this.deleteById(token.getId());
            }
            log.info("清理过期 Token：共清理 {} 条记录", expiredTokens.size());
            return expiredTokens.size();
        }
        
        return 0;
    }
}
