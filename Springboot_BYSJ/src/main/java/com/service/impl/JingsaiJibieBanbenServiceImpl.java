package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiJibieBanbenDao;
import com.entity.JingsaiJibieBanbenEntity;
import com.service.JingsaiJibieBanbenService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("jingsaiJibieBanbenService")
@Slf4j
public class JingsaiJibieBanbenServiceImpl extends ServiceImpl<JingsaiJibieBanbenDao, JingsaiJibieBanbenEntity> implements JingsaiJibieBanbenService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
        
        // 竞赛名称模糊查询
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.debug("筛选竞赛名称: {}", params.get("jingsaimingcheng"));
        }
        
        // 级别精确匹配
        if (params.get("jibie") != null && !params.get("jibie").toString().isEmpty()) {
            ew.eq("jibie", params.get("jibie").toString());
            log.debug("筛选级别: {}", params.get("jibie"));
        }
        
        Page<JingsaiJibieBanbenEntity> page = this.selectPage(
            new Query<JingsaiJibieBanbenEntity>(params).getPage(),
            ew
        );
        log.debug("级别版本查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public JingsaiJibieBanbenEntity getByDate(Long jingsaiId, Date queryDate) {
        return baseMapper.getByDate(jingsaiId, queryDate);
    }

    @Override
    public JingsaiJibieBanbenEntity getCurrentVersion(Long jingsaiId) {
        return baseMapper.getCurrentVersion(jingsaiId);
    }

    /**
     * 创建级别版本（自动处理旧版本）
     */
    @Override
    @Transactional
    public R saveBanben(JingsaiJibieBanbenEntity banben, String caozuoRen) {
        try {
            // 1. 将该竞赛的其他版本 is_current 设为"否"
            EntityWrapper<JingsaiJibieBanbenEntity> ew = new EntityWrapper<>();
            ew.eq("jingsai_id", banben.getJingsaiId());
            ew.eq("is_current", "是");
            List<JingsaiJibieBanbenEntity> oldVersions = this.selectList(ew);

            for (JingsaiJibieBanbenEntity oldVersion : oldVersions) {
                oldVersion.setIsCurrent("否");
                // 设置旧版本的失效日期为新版本生效日期的前一天
                if (banben.getShengxiaoRiqi() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(banben.getShengxiaoRiqi());
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    oldVersion.setShixiaoRiqi(cal.getTime());
                }
                this.updateById(oldVersion);
            }

            // 2. 保存新版本
            banben.setId(IdWorker.getId());
            banben.setCaozuoRen(caozuoRen);
            banben.setIsCurrent("是");
            banben.setShixiaoRiqi(null); // 新版本无失效日期
            this.insert(banben);

            return R.ok("级别版本创建成功");
        } catch (Exception e) {
            throw new RuntimeException("创建级别版本失败：" + e.getMessage());
        }
    }
}
