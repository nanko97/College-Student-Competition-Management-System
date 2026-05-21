package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.TuanduiApplyDao;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.entity.TuanduiApplyEntity;
import com.entity.XueshengEntity;
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

    @Autowired
    private com.service.XiaoxiTongzhiService xiaoxiTongzhiService;

    @Autowired
    private com.service.XueshengService xueshengService;

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
    @Transactional(rollbackFor = Exception.class)
    public R applyJoin(TuanduiApplyEntity apply) {
        try {
            log.info("接收到的申请数据 - tuanduiId: {}, 类型: {}", apply.getTuanduiId(), apply.getTuanduiId() != null ? apply.getTuanduiId().getClass().getName() : "null");
            
            // 容错处理：如果tuanduiId为null，可能是精度丢失导致，尝试从其他方式获取
            if (apply.getTuanduiId() == null) {
                log.error("团队ID为空，无法处理申请");
                return R.error("团队ID无效");
            }
            
            // 1. 验证团队是否存在
            JingsaiTuanduiEntity tuandui = tuanduiService.selectById(apply.getTuanduiId());
            if (tuandui == null) {
                log.error("团队不存在，ID: {}", apply.getTuanduiId());
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

            // ========== 3.5 兜底处理：如果学生姓名为空，从学生表查询填充 ==========
            if (apply.getXueshengxingming() == null || apply.getXueshengxingming().isEmpty()) {
                log.warn("学生姓名为空，从学生表查询 - 学号: {}", apply.getXuehao());
                EntityWrapper<XueshengEntity> xueshengEw = new EntityWrapper<>();
                xueshengEw.eq("xuehao", apply.getXuehao());
                XueshengEntity xuesheng = xueshengService.selectOne(xueshengEw);
                if (xuesheng != null && xuesheng.getXueshengxingming() != null) {
                    apply.setXueshengxingming(xuesheng.getXueshengxingming());
                    log.info("从学生表查询到姓名: {}", apply.getXueshengxingming());
                } else {
                    log.error("未找到学生信息，学号: {}", apply.getXuehao());
                    return R.error("未找到学生信息，学号: " + apply.getXuehao());
                }
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

            // 6. 发送申请加入通知给团队负责人
            try {
                String fuzerenXuehao = tuandui.getFuzerenXuehao();
                if (fuzerenXuehao != null && !fuzerenXuehao.isEmpty()) {
                    String studentName = apply.getXueshengxingming() != null ? apply.getXueshengxingming() : apply.getXuehao();
                    String tuanduiMingcheng = tuandui.getTuanduiMingcheng();
                    String title = "团队加入申请";
                    String content = String.format("学生「%s」申请加入您的团队「%s」，请及时审核。", studentName, tuanduiMingcheng);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "团队申请", "系统",
                        fuzerenXuehao, null, "xuesheng",
                        apply.getId(), "tuanduiapply"
                    );
                    log.info("发送团队加入申请通知给负责人: {}, 申请ID: {}", fuzerenXuehao, apply.getId());
                }
            } catch (Exception e) {
                log.error("发送团队加入申请通知异常", e);
            }

            log.info("学生 {} 申请加入团队 {}", apply.getXueshengxingming(), tuandui.getTuanduiMingcheng());
            return R.ok("申请已提交，等待负责人审核");

        } catch (Exception e) {
            log.error("申请加入团队失败", e);
            throw new RuntimeException("申请失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

            // 4.5 兜底处理：如果学生姓名为空，从学生表查询填充
            if (apply.getXueshengxingming() == null || apply.getXueshengxingming().isEmpty()) {
                log.warn("退出申请-学生姓名为空，从学生表查询 - 学号: {}", apply.getXuehao());
                EntityWrapper<XueshengEntity> xueshengEw = new EntityWrapper<>();
                xueshengEw.eq("xuehao", apply.getXuehao());
                XueshengEntity xuesheng = xueshengService.selectOne(xueshengEw);
                if (xuesheng != null && xuesheng.getXueshengxingming() != null) {
                    apply.setXueshengxingming(xuesheng.getXueshengxingming());
                    log.info("退出申请-从学生表查询到姓名: {}", apply.getXueshengxingming());
                } else {
                    log.error("退出申请-未找到学生信息，学号: {}", apply.getXuehao());
                    return R.error("未找到学生信息，学号: " + apply.getXuehao());
                }
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

            // 7. 发送申请退出通知给团队负责人
            try {
                String fuzerenXuehao = tuandui.getFuzerenXuehao();
                if (fuzerenXuehao != null && !fuzerenXuehao.isEmpty()) {
                    String studentName = apply.getXueshengxingming() != null ? apply.getXueshengxingming() : apply.getXuehao();
                    String tuanduiMingcheng = tuandui.getTuanduiMingcheng();
                    String title = "团队退出申请";
                    String content = String.format("学生「%s」申请退出您的团队「%s」，请及时审核。", studentName, tuanduiMingcheng);
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "团队申请", "系统",
                        fuzerenXuehao, null, "xuesheng",
                        apply.getId(), "tuanduiapply"
                    );
                    log.info("发送团队退出申请通知给负责人: {}, 申请ID: {}", fuzerenXuehao, apply.getId());
                }
            } catch (Exception e) {
                log.error("发送团队退出申请通知异常", e);
            }

            log.info("学生 {} 申请退出团队 {}", apply.getXueshengxingming(), tuandui.getTuanduiMingcheng());
            return R.ok("申请已提交，等待负责人审核");

        } catch (Exception e) {
            log.error("申请退出团队失败", e);
            throw new RuntimeException("申请失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

            // 发送审核结果通知给申请人
            try {
                String applicantXuehao = apply.getXuehao();
                if (applicantXuehao != null && !applicantXuehao.isEmpty()) {
                    String tuanduiMingcheng = apply.getTuanduiMingcheng() != null ? apply.getTuanduiMingcheng() : "";
                    String applyType = apply.getApplyType(); // 加入/退出
                    String title;
                    String content;
                    if ("已通过".equals(zhuangtai)) {
                        title = String.format("团队%s申请通过", applyType);
                        content = String.format("您申请%s团队「%s」已通过审核。", applyType, tuanduiMingcheng);
                    } else {
                        title = String.format("团队%s申请未通过", applyType);
                        content = String.format("您申请%s团队「%s」未通过审核。", applyType, tuanduiMingcheng);
                        if (shenheYijian != null && !shenheYijian.isEmpty()) {
                            content += "审核意见：" + shenheYijian;
                        }
                    }
                    xiaoxiTongzhiService.sendTongzhi(
                        title, content, "团队申请", shenheXingming,
                        applicantXuehao, null, "xuesheng",
                        applyId, "tuanduiapply"
                    );
                    log.info("发送团队申请审核结果通知给申请人: {}, 申请ID: {}, 结果: {}", applicantXuehao, applyId, zhuangtai);
                }
            } catch (Exception e) {
                log.error("发送团队申请审核结果通知异常", e);
            }

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
