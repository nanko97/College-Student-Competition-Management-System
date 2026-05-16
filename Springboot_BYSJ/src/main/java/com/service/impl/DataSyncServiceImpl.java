package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.*;
import com.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据联动同步服务实现
 */
@Service("dataSyncService")
@Slf4j
public class DataSyncServiceImpl implements DataSyncService {

    @Autowired
    private JingsaiTuanduiChengyuanService chengyuanService;
    
    @Autowired
    private JingsaibaomingService baomingService;
    
    @Autowired
    private ZuopindafenService zuopindafenService;
    
    @Autowired
    private ZuopindafenFuheService zuopindafenFuheService;
    
    @Autowired
    private JingsaiJiaofeiJiluService jiaofeiJiluService;
    
    @Autowired
    private JingsaixinxiService jingsaixinxiService;
    
    @Autowired
    private TuanduiApplyService tuanduiApplyService;

    /**
     * 学生信息变更同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncXueshengInfo(XueshengEntity xuesheng) {
        String xuehao = xuesheng.getXuehao();
        if (xuehao == null || xuehao.isEmpty()) {
            log.warn("学生学号为空，跳过数据同步");
            return;
        }

        log.info("开始同步学生信息 - 学号: {}, 姓名: {}", xuehao, xuesheng.getXueshengxingming());

        // 1. 同步团队成员表
        try {
            EntityWrapper<JingsaiTuanduiChengyuanEntity> ew1 = new EntityWrapper<>();
            ew1.eq("xuehao", xuehao);
            JingsaiTuanduiChengyuanEntity chengyuan = new JingsaiTuanduiChengyuanEntity();
            chengyuan.setXueshengxingming(xuesheng.getXueshengxingming());
            chengyuan.setXueyuan(xuesheng.getXueyuanmingcheng());
            chengyuan.setBanji(xuesheng.getBanji());
            chengyuan.setShouji(xuesheng.getShouji());
            chengyuan.setYouxiang(xuesheng.getYouxiang());
            boolean success1 = chengyuanService.update(chengyuan, ew1);
            if (success1) {
                log.info("更新团队成员表成功");
            }
        } catch (Exception e) {
            log.error("同步团队成员表失败", e);
        }

        // 2. 同步竞赛报名表
        try {
            EntityWrapper<JingsaibaomingEntity> ew2 = new EntityWrapper<>();
            ew2.eq("xuehao", xuehao);
            JingsaibaomingEntity baoming = new JingsaibaomingEntity();
            baoming.setXueshengxingming(xuesheng.getXueshengxingming());
            boolean success2 = baomingService.update(baoming, ew2);
            if (success2) {
                log.info("更新竞赛报名表成功");
            }
        } catch (Exception e) {
            log.error("同步竞赛报名表失败", e);
        }

        // 3. 同步作品打分表
        try {
            EntityWrapper<ZuopindafenEntity> ew3 = new EntityWrapper<>();
            ew3.eq("xuehao", xuehao);
            ZuopindafenEntity zuopindafen = new ZuopindafenEntity();
            zuopindafen.setXueshengxingming(xuesheng.getXueshengxingming());
            boolean success3 = zuopindafenService.update(zuopindafen, ew3);
            if (success3) {
                log.info("更新作品打分表成功");
            }
        } catch (Exception e) {
            log.error("同步作品打分表失败", e);
        }

        // 4. 同步作品复核表
        try {
            EntityWrapper<ZuopindafenFuheEntity> ew4 = new EntityWrapper<>();
            ew4.eq("xuehao", xuehao);
            ZuopindafenFuheEntity fuhe = new ZuopindafenFuheEntity();
            fuhe.setXueshengxingming(xuesheng.getXueshengxingming());
            boolean success4 = zuopindafenFuheService.update(fuhe, ew4);
            if (success4) {
                log.info("更新作品复核表成功");
            }
        } catch (Exception e) {
            log.error("同步作品复核表失败", e);
        }

        // 5. 同步缴费记录表
        try {
            EntityWrapper<JingsaiJiaofeiJiluEntity> ew5 = new EntityWrapper<>();
            ew5.eq("xuehao", xuehao);
            JingsaiJiaofeiJiluEntity jiaofei = new JingsaiJiaofeiJiluEntity();
            jiaofei.setXueshengxingming(xuesheng.getXueshengxingming());
            boolean success5 = jiaofeiJiluService.update(jiaofei, ew5);
            if (success5) {
                log.info("更新缴费记录表成功");
            }
        } catch (Exception e) {
            log.error("同步缴费记录表失败", e);
        }

        // 6. 同步团队申请表
        try {
            EntityWrapper<TuanduiApplyEntity> ew6 = new EntityWrapper<>();
            ew6.eq("xuehao", xuehao);
            TuanduiApplyEntity apply = new TuanduiApplyEntity();
            apply.setXueshengxingming(xuesheng.getXueshengxingming());
            boolean success6 = tuanduiApplyService.update(apply, ew6);
            if (success6) {
                log.info("更新团队申请表成功");
            }
        } catch (Exception e) {
            log.error("同步团队申请表失败", e);
        }

        log.info("学生信息同步完成 - 学号: {}", xuehao);
    }

    /**
     * 教师信息变更同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncJiaoshiInfo(JiaoshiEntity jiaoshi) {
        String gonghao = jiaoshi.getGonghao();
        if (gonghao == null || gonghao.isEmpty()) {
            log.warn("教师工号为空，跳过数据同步");
            return;
        }

        log.info("开始同步教师信息 - 工号: {}, 姓名: {}", gonghao, jiaoshi.getJiaoshixingming());

        // 1. 同步竞赛报名表
        try {
            EntityWrapper<JingsaibaomingEntity> ew1 = new EntityWrapper<>();
            ew1.eq("gonghao", gonghao);
            JingsaibaomingEntity baoming = new JingsaibaomingEntity();
            baoming.setJiaoshixingming(jiaoshi.getJiaoshixingming());
            boolean success1 = baomingService.update(baoming, ew1);
            if (success1) {
                log.info("更新竞赛报名表成功");
            }
        } catch (Exception e) {
            log.error("同步竞赛报名表失败", e);
        }

        // 2. 同步作品打分表
        try {
            EntityWrapper<ZuopindafenEntity> ew2 = new EntityWrapper<>();
            ew2.eq("gonghao", gonghao);
            ZuopindafenEntity zuopindafen = new ZuopindafenEntity();
            zuopindafen.setJiaoshixingming(jiaoshi.getJiaoshixingming());
            boolean success2 = zuopindafenService.update(zuopindafen, ew2);
            if (success2) {
                log.info("更新作品打分表成功");
            }
        } catch (Exception e) {
            log.error("同步作品打分表失败", e);
        }

        // 3. 同步竞赛信息表
        try {
            EntityWrapper<JingsaixinxiEntity> ew3 = new EntityWrapper<>();
            ew3.eq("gonghao", gonghao);
            JingsaixinxiEntity jingsai = new JingsaixinxiEntity();
            jingsai.setJiaoshixingming(jiaoshi.getJiaoshixingming());
            boolean success3 = jingsaixinxiService.update(jingsai, ew3);
            if (success3) {
                log.info("更新竞赛信息表成功");
            }
        } catch (Exception e) {
            log.error("同步竞赛信息表失败", e);
        }

        // 4. 同步作品复核表（审核教师）
        try {
            EntityWrapper<ZuopindafenFuheEntity> ew4 = new EntityWrapper<>();
            ew4.eq("shenhe_gonghao", gonghao);
            ZuopindafenFuheEntity fuhe = new ZuopindafenFuheEntity();
            fuhe.setShenheJiaoshi(jiaoshi.getJiaoshixingming());
            boolean success4 = zuopindafenFuheService.update(fuhe, ew4);
            if (success4) {
                log.info("更新作品复核表成功");
            }
        } catch (Exception e) {
            log.error("同步作品复核表失败", e);
        }

        log.info("教师信息同步完成 - 工号: {}", gonghao);
    }
}
