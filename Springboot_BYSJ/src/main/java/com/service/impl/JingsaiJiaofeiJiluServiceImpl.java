package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiJiaofeiJiluDao;
import com.entity.JingsaiJiaofeiJiluEntity;
import com.entity.JingsaibaomingEntity;
import com.service.JingsaiJiaofeiJiluService;
import com.service.JingsaibaomingService;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("jingsaiJiaofeiJiluService")
@Slf4j
public class JingsaiJiaofeiJiluServiceImpl extends ServiceImpl<JingsaiJiaofeiJiluDao, JingsaiJiaofeiJiluEntity> implements JingsaiJiaofeiJiluService {

    @Autowired
    private JingsaibaomingService jingsaibaomingService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiJiaofeiJiluEntity> ew = new EntityWrapper<>();
        
        // 竞赛名称模糊查询
        if (params.get("jingsaimingcheng") != null && !params.get("jingsaimingcheng").toString().isEmpty()) {
            ew.like("jingsaimingcheng", params.get("jingsaimingcheng").toString());
            log.debug("筛选项：竞赛名称 = {}", params.get("jingsaimingcheng"));
        }
        
        // 缴费状态精确匹配
        if (params.get("jiaofeiZhuangtai") != null && !params.get("jiaofeiZhuangtai").toString().isEmpty()) {
            ew.eq("jiaofei_zhuangtai", params.get("jiaofeiZhuangtai").toString());
            log.debug("筛选项：缴费状态 = {}", params.get("jiaofeiZhuangtai"));
        }
        
        Page<JingsaiJiaofeiJiluEntity> page = this.selectPage(
            new Query<JingsaiJiaofeiJiluEntity>(params).getPage(),
            ew
        );
        log.debug("缴费记录查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJiaofeiJiluEntity> wrapper) {
        Page<JingsaiJiaofeiJiluEntity> page = new Query<JingsaiJiaofeiJiluEntity>(params).getPage();
        // 使用标准分页查询，确保ID正确映射
        Page<JingsaiJiaofeiJiluEntity> resultPage = this.selectPage(page, wrapper);
        return new PageUtils(resultPage);
    }

    /**
     * 审核缴费
     */
    @Override
    @Transactional
    public R shenheJiaofei(Long jiaofeiId, String zhuangtai, String yijian, String shenheRen) {
        try {
            JingsaiJiaofeiJiluEntity jiaofei = this.selectById(jiaofeiId);
            if (jiaofei == null) {
                return R.error("缴费记录不存在");
            }

            // 更新审核信息
            jiaofei.setJiaofeiZhuangtai(zhuangtai);
            jiaofei.setShenheRen(shenheRen);
            jiaofei.setShenheShijian(new Date());
            jiaofei.setShenheYijian(yijian);
            this.updateById(jiaofei);

            // 更新报名表的支付状态
            JingsaibaomingEntity baoming = jingsaibaomingService.selectById(jiaofei.getBaomingId());
            if (baoming != null) {
                if ("已通过".equals(zhuangtai)) {
                    baoming.setIspay("已支付");
                } else if ("已驳回".equals(zhuangtai)) {
                    baoming.setIspay("已驳回");
                }
                jingsaibaomingService.updateById(baoming);
            }

            return R.ok("审核完成");
        } catch (Exception e) {
            throw new RuntimeException("审核缴费失败：" + e.getMessage());
        }
    }
}
