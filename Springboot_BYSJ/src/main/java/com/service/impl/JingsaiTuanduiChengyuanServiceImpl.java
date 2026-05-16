package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiSaidaoDao;
import com.dao.JingsaiTuanduiChengyuanDao;
import com.entity.JingsaiSaidaoEntity;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.service.JingsaiTuanduiChengyuanService;
import com.service.JingsaiTuanduiService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Map;

@Service("jingsaiTuanduiChengyuanService")
@Slf4j
public class JingsaiTuanduiChengyuanServiceImpl extends ServiceImpl<JingsaiTuanduiChengyuanDao, JingsaiTuanduiChengyuanEntity> implements JingsaiTuanduiChengyuanService {

    @Autowired
    private JingsaiTuanduiService tuanduiService;

    @Autowired
    private JingsaiSaidaoDao saidaoDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiTuanduiChengyuanEntity> ew = new EntityWrapper<>();
        
        // 学号精确匹配
        if (params.get("xuehao") != null && !params.get("xuehao").toString().isEmpty()) {
            ew.eq("xuehao", params.get("xuehao").toString());
            log.debug("筛选项：学号 = {}", params.get("xuehao"));
        }
        
        // 学生姓名模糊查询
        if (params.get("xueshengxingming") != null && !params.get("xueshengxingming").toString().isEmpty()) {
            ew.like("xueshengxingming", params.get("xueshengxingming").toString());
            log.debug("筛选项：学生姓名 = {}", params.get("xueshengxingming"));
        }
        
        Page<JingsaiTuanduiChengyuanEntity> page = this.selectPage(new Query<JingsaiTuanduiChengyuanEntity>(params).getPage(), ew);
        log.debug("团队成员查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    /**
     * 添加团队成员
     */
    @Override
    @Transactional
    public R addChengyuan(JingsaiTuanduiChengyuanEntity chengyuan) {
        try {
            // 1. 验证团队存在
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(chengyuan.getTuanduiId());
            if (tuandui == null) {
                return R.error("团队不存在");
            }

            // 2. 验证学生未在其他团队中
            EntityWrapper<JingsaiTuanduiChengyuanEntity> ew = new EntityWrapper<>();
            ew.eq("xuehao", chengyuan.getXuehao());
            ew.eq("is_active", "是");
            Integer count = this.selectCount(ew);
            if (count > 0) {
                return R.error("该学生已在其他团队中");
            }

            // 3. 验证团队人数上限（根据赛道配置）
            if (tuandui.getSaidaoId() != null) {
                JingsaiSaidaoEntity saidao = saidaoDao.selectById(tuandui.getSaidaoId());
                if (saidao != null && saidao.getTuanduiRenshuMax() != null) {
                    int currentCount = tuandui.getChengyuanRenshu() != null ? tuandui.getChengyuanRenshu() : 0;
                    if (currentCount >= saidao.getTuanduiRenshuMax()) {
                        return R.error("团队人数已达上限（" + saidao.getTuanduiRenshuMax() + "人），无法继续添加成员");
                    }
                }
            }

            // 4. 保存成员
            chengyuan.setId(IdWorker.getId());
            chengyuan.setTuanduiBianhao(tuandui.getTuanduiBianhao());
            chengyuan.setJuese("队员");
            chengyuan.setIsActive("是");
            chengyuan.setRuzuiShijian(new Date());
            this.insert(chengyuan);

            // 5. 更新团队人数
            tuandui.setChengyuanRenshu(tuandui.getChengyuanRenshu() + 1);
            tuanduiService.updateById(tuandui);

            return R.ok("添加成员成功");
        } catch (Exception e) {
            throw new RuntimeException("添加成员失败：" + e.getMessage());
        }
    }
}
