package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.TuanduiApplyDao;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.entity.TuanduiApplyEntity;
import com.service.JingsaiTuanduiChengyuanService;
import com.service.JingsaiTuanduiService;
import com.service.TuanduiApplyService;
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

/**
 * 团队申请Service实现
 */
@Service("tuanduiApplyService")
@Slf4j
public class TuanduiApplyServiceImpl extends ServiceImpl<TuanduiApplyDao, TuanduiApplyEntity> implements TuanduiApplyService {

    @Autowired
    private JingsaiTuanduiService tuanduiService;

    @Autowired
    private JingsaiTuanduiChengyuanService chengyuanService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TuanduiApplyEntity> page = this.selectPage(
            new Query<TuanduiApplyEntity>(params).getPage(),
            new EntityWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, EntityWrapper<TuanduiApplyEntity> ew) {
        Page<TuanduiApplyEntity> page = this.selectPage(
            new Query<TuanduiApplyEntity>(params).getPage(),
            ew
        );
        return new PageUtils(page);
    }

    @Override
    @Transactional
    public R applyJoin(TuanduiApplyEntity apply) {
        try {
            // 1. 验证团队是否存在
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(apply.getTuanduiId());
            if (tuandui == null) {
                return R.error("团队不存在");
            }

            // 2. 检查是否已经是团队成员
            EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
            memberEw.eq("tuandui_id", apply.getTuanduiId());
            memberEw.eq("xuehao", apply.getXuehao());
            memberEw.eq("is_active", "是");
            if (chengyuanService.selectCount(memberEw) > 0) {
                return R.error("您已经是该团队成员");
            }

            // 3. 检查是否有待审核的申请
            EntityWrapper<TuanduiApplyEntity> applyEw = new EntityWrapper<>();
            applyEw.eq("tuandui_id", apply.getTuanduiId());
            applyEw.eq("xuehao", apply.getXuehao());
            applyEw.eq("apply_type", "加入");
            applyEw.eq("apply_status", "待审核");
            if (this.selectCount(applyEw) > 0) {
                return R.error("您已有待审核的加入申请");
            }

            // 4. 设置申请信息
            apply.setApplyType("加入");
            apply.setApplyStatus("待审核");
            apply.setTuanduiMingcheng(tuandui.getTuanduiMingcheng());
            apply.setJingsaiId(tuandui.getJingsaiId());
            apply.setJingsaimingcheng(tuandui.getJingsaimingcheng());
            apply.setApplyTime(new Date());

            // 5. 保存申请
            this.insert(apply);

            log.info("学生 {} 申请加入团队 {}", apply.getXueshengxingming(), tuandui.getTuanduiMingcheng());
            return R.ok("申请已提交，等待负责人审核");

        } catch (Exception e) {
            log.error("申请加入团队失败", e);
            throw new RuntimeException("申请失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R applyQuit(TuanduiApplyEntity apply) {
        try {
            // 1. 验证团队是否存在
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(apply.getTuanduiId());
            if (tuandui == null) {
                return R.error("团队不存在");
            }

            // 2. 检查是否是团队成员
            EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
            memberEw.eq("tuandui_id", apply.getTuanduiId());
            memberEw.eq("xuehao", apply.getXuehao());
            memberEw.eq("is_active", "是");
            List<JingsaiTuanduiChengyuanEntity> members = chengyuanService.selectList(memberEw);

            if (members.isEmpty()) {
                return R.error("您不是该团队成员");
            }

            JingsaiTuanduiChengyuanEntity chengyuan = members.get(0);

            // 3. 负责人不能直接退出，需要先转让
            if ("负责人".equals(chengyuan.getJuese())) {
                return R.error("负责人不能退出团队，请先转让负责人身份或联系教师处理");
            }

            // 4. 检查是否有待审核的退出申请
            EntityWrapper<TuanduiApplyEntity> applyEw = new EntityWrapper<>();
            applyEw.eq("tuandui_id", apply.getTuanduiId());
            applyEw.eq("xuehao", apply.getXuehao());
            applyEw.eq("apply_type", "退出");
            applyEw.eq("apply_status", "待审核");
            if (this.selectCount(applyEw) > 0) {
                return R.error("您已有待审核的退出申请");
            }

            // 5. 设置申请信息
            apply.setApplyType("退出");
            apply.setApplyStatus("待审核");
            apply.setTuanduiMingcheng(tuandui.getTuanduiMingcheng());
            apply.setJingsaiId(tuandui.getJingsaiId());
            apply.setJingsaimingcheng(tuandui.getJingsaimingcheng());
            apply.setApplyTime(new Date());

            // 6. 保存申请
            this.insert(apply);

            log.info("学生 {} 申请退出团队 {}", apply.getXueshengxingming(), tuandui.getTuanduiMingcheng());
            return R.ok("申请已提交，等待负责人审核");

        } catch (Exception e) {
            log.error("申请退出团队失败", e);
            throw new RuntimeException("申请失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R shenheApply(Long applyId, String zhuangtai, String shenheYijian, String shenheXuehao, String shenheXingming) {
        try {
            // 1. 查询申请记录
            TuanduiApplyEntity apply = this.selectById(applyId);
            if (apply == null) {
                return R.error("申请记录不存在");
            }

            // 2. 检查状态
            if (!"待审核".equals(apply.getApplyStatus())) {
                return R.error("该申请已经审核过");
            }

            // 3. 更新审核信息
            apply.setApplyStatus(zhuangtai);
            apply.setShenheYijian(shenheYijian);
            apply.setShenheXuehao(shenheXuehao);
            apply.setShenheXingming(shenheXingming);
            apply.setShenheTime(new Date());
            this.updateById(apply);

            // 4. 如果通过，执行相应操作
            if ("已通过".equals(zhuangtai)) {
                if ("加入".equals(apply.getApplyType())) {
                    // 执行加入团队操作
                    executeJoinTeam(apply);
                } else if ("退出".equals(apply.getApplyType())) {
                    // 执行退出团队操作
                    executeQuitTeam(apply);
                }
            }

            log.info("团队申请审核完成 - ID: {}, 类型: {}, 结果: {}", applyId, apply.getApplyType(), zhuangtai);
            return R.ok("审核完成");

        } catch (Exception e) {
            log.error("审核团队申请失败", e);
            throw new RuntimeException("审核失败：" + e.getMessage());
        }
    }

    /**
     * 执行加入团队操作
     */
    private void executeJoinTeam(TuanduiApplyEntity apply) {
        // 1. 检查团队人数上限
        JingsaiTuanduiEntity tuandui = tuanduiService.selectById(apply.getTuanduiId());
        if (tuandui != null && tuandui.getChengyuanRenshu() != null) {
            // 这里可以根据赛道配置的人数上限进行检查
            // 暂时不限制
        }

        // 2. 添加为团队成员
        JingsaiTuanduiChengyuanEntity chengyuan = new JingsaiTuanduiChengyuanEntity();
        chengyuan.setTuanduiId(apply.getTuanduiId());
        chengyuan.setTuanduiBianhao(tuandui.getTuanduiBianhao());
        chengyuan.setXuehao(apply.getXuehao());
        chengyuan.setXueshengxingming(apply.getXueshengxingming());
        chengyuan.setJuese("成员");
        chengyuan.setIsActive("是");
        chengyuan.setRuzuiShijian(new Date());
        chengyuanService.insert(chengyuan);

        // 3. 更新团队人数
        tuandui.setChengyuanRenshu(tuandui.getChengyuanRenshu() + 1);
        tuanduiService.updateById(tuandui);

        log.info("学生 {} 成功加入团队 {}", apply.getXueshengxingming(), apply.getTuanduiMingcheng());
    }

    /**
     * 执行退出团队操作
     */
    private void executeQuitTeam(TuanduiApplyEntity apply) {
        // 1. 查找成员记录
        EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
        memberEw.eq("tuandui_id", apply.getTuanduiId());
        memberEw.eq("xuehao", apply.getXuehao());
        memberEw.eq("is_active", "是");
        List<JingsaiTuanduiChengyuanEntity> members = chengyuanService.selectList(memberEw);

        if (!members.isEmpty()) {
            // 2. 标记为非活跃
            JingsaiTuanduiChengyuanEntity chengyuan = members.get(0);
            chengyuan.setIsActive("否");
            chengyuanService.updateById(chengyuan);

            // 3. 更新团队人数
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(apply.getTuanduiId());
            if (tuandui != null && tuandui.getChengyuanRenshu() > 1) {
                tuandui.setChengyuanRenshu(tuandui.getChengyuanRenshu() - 1);
                tuanduiService.updateById(tuandui);
            }

            log.info("学生 {} 成功退出团队 {}", apply.getXueshengxingming(), apply.getTuanduiMingcheng());
        }
    }
}
