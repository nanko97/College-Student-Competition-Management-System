package com.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiRenyuanBianguengDao;
import com.dao.JingsaiTuanduiDao;
import com.entity.JingsaiRenyuanBianguengEntity;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.service.JingsaiRenyuanBianguengService;
import com.service.JingsaiTuanduiChengyuanService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("jingsaiRenyuanBianguengService")
public class JingsaiRenyuanBianguengServiceImpl extends ServiceImpl<JingsaiRenyuanBianguengDao, JingsaiRenyuanBianguengEntity> implements JingsaiRenyuanBianguengService {

    @Autowired
    private JingsaiTuanduiChengyuanService chengyuanService;

    @Autowired
    private JingsaiTuanduiDao tuanduiDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiRenyuanBianguengEntity> ew = new EntityWrapper<>();
        
        // 教师权限过滤：只查询特定竞赛ID列表的变更记录
        if (params.get("jingsai_id_in") != null) {
            @SuppressWarnings("unchecked")
            java.util.List<Long> jingsaiIds = (java.util.List<Long>) params.get("jingsai_id_in");
            if (jingsaiIds != null && !jingsaiIds.isEmpty()) {
                ew.in("jingsai_id", jingsaiIds);
                log.debug("教师权限过滤 - 竞赛ID列表: {}", jingsaiIds);
            }
        }
        
        // 教师没有创建任何竞赛时，返回空列表
        if (params.get("jingsai_id") != null && "-1".equals(params.get("jingsai_id").toString())) {
            log.info("教师没有创建任何竞赛，直接返回空列表");
            return new PageUtils(new Page<JingsaiRenyuanBianguengEntity>());
        }
        
        // 变更类型精确匹配
        if (params.get("bianguengLeixing") != null && !params.get("bianguengLeixing").toString().isEmpty()) {
            ew.eq("biangueng_leixing", params.get("bianguengLeixing").toString());
            log.debug("筛选项：变更类型 = {}", params.get("bianguengLeixing"));
        }
        
        // 审核状态精确匹配
        if (params.get("shenheZhuangtai") != null && !params.get("shenheZhuangtai").toString().isEmpty()) {
            ew.eq("shenhe_zhuangtai", params.get("shenheZhuangtai").toString());
            log.debug("筛选项：审核状态 = {}", params.get("shenheZhuangtai"));
        }
        
        Page<JingsaiRenyuanBianguengEntity> page = this.selectPage(new Query<JingsaiRenyuanBianguengEntity>(params).getPage(), ew);
        log.debug("人员变更记录查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiRenyuanBianguengEntity> wrapper) {
        Page<JingsaiRenyuanBianguengEntity> page = new Query<JingsaiRenyuanBianguengEntity>(params).getPage();
        List<JingsaiRenyuanBianguengEntity> records = baseMapper.selectListView(wrapper);
        page.setTotal((long) records.size());
        page.setRecords(records);
        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R applyBiangueng(JingsaiRenyuanBianguengEntity biangueng, String caozuoRen) {
        try {
            biangueng.setId(IdWorker.getId());
            biangueng.setCaozuoRenXuehao(caozuoRen);
            biangueng.setShenheZhuangtai("待审核");
            this.insert(biangueng);
            return R.ok("变更申请已提交，等待审核");
        } catch (Exception e) {
            throw new RuntimeException("申请变更失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R shenheBiangueng(Long bianguengId, String zhuangtai, String shenheRen) {
        try {
            JingsaiRenyuanBianguengEntity biangueng = this.selectById(bianguengId);
            if (biangueng == null) {
                return R.error("变更记录不存在");
            }

            // 幂等保护：防止重复审核
            if (!"待审核".equals(biangueng.getShenheZhuangtai())) {
                return R.error("该变更申请已审核，请勿重复操作");
            }

            biangueng.setShenheZhuangtai(zhuangtai);
            biangueng.setShenheRen(shenheRen);
            biangueng.setShenheShijian(new Date());
            this.updateById(biangueng);

            // 如果通过，执行实际变更
            if ("已通过".equals(zhuangtai)) {
                executeBiangueng(biangueng);
            }

            return R.ok("审核完成");
        } catch (Exception e) {
            throw new RuntimeException("审核变更失败：" + e.getMessage());
        }
    }

    /**
     * 执行人员变更操作
     */
    private void executeBiangueng(JingsaiRenyuanBianguengEntity biangueng) {
        String bianguengType = biangueng.getBianguengLeixing();
        Long tuanduiId = biangueng.getTuanduiId();

        if ("添加成员".equals(bianguengType)) {
            // 解析操作后数据并添加成员
            JSONObject newData = JSONObject.parseObject(biangueng.getCaozuoHou());
            JingsaiTuanduiChengyuanEntity chengyuan = new JingsaiTuanduiChengyuanEntity();
            chengyuan.setTuanduiId(tuanduiId);
            chengyuan.setXuehao(newData.getString("xuehao"));
            chengyuan.setXueshengxingming(newData.getString("xueshengxingming"));
            chengyuan.setJuese(newData.getString("juese"));
            chengyuan.setXueyuan(newData.getString("xueyuan"));
            chengyuan.setZhuanye(newData.getString("zhuanye"));
            chengyuan.setBanji(newData.getString("banji"));
            chengyuan.setIsActive("是");
            chengyuanService.insert(chengyuan);

            // 更新团队人数
            updateTuanduiRenshu(tuanduiId, 1);

        } else if ("移除成员".equals(bianguengType)) {
            // 解析操作前数据并移除成员
            JSONObject oldData = JSONObject.parseObject(biangueng.getCaozuoQian());
            EntityWrapper<JingsaiTuanduiChengyuanEntity> ew = new EntityWrapper<>();
            ew.eq("tuandui_id", tuanduiId);
            ew.eq("xuehao", oldData.getString("xuehao"));
            List<JingsaiTuanduiChengyuanEntity> members = chengyuanService.selectList(ew);
            if (!members.isEmpty()) {
                chengyuanService.deleteById(members.get(0).getId());
                // 更新团队人数
                updateTuanduiRenshu(tuanduiId, -1);
            }

        } else if ("更换负责人".equals(bianguengType)) {
            // 解析操作前后数据并更换负责人
            JSONObject oldData = JSONObject.parseObject(biangueng.getCaozuoQian());
            JSONObject newData = JSONObject.parseObject(biangueng.getCaozuoHou());

            // 将原负责人改为队员
            EntityWrapper<JingsaiTuanduiChengyuanEntity> ewOld = new EntityWrapper<>();
            ewOld.eq("tuandui_id", tuanduiId);
            ewOld.eq("xuehao", oldData.getString("xuehao"));
            List<JingsaiTuanduiChengyuanEntity> oldMembers = chengyuanService.selectList(ewOld);
            if (!oldMembers.isEmpty()) {
                JingsaiTuanduiChengyuanEntity oldLeader = oldMembers.get(0);
                oldLeader.setJuese("队员");
                chengyuanService.updateById(oldLeader);
            }

            // 将新负责人角色更新
            EntityWrapper<JingsaiTuanduiChengyuanEntity> ewNew = new EntityWrapper<>();
            ewNew.eq("tuandui_id", tuanduiId);
            ewNew.eq("xuehao", newData.getString("xuehao"));
            List<JingsaiTuanduiChengyuanEntity> newMembers = chengyuanService.selectList(ewNew);
            if (!newMembers.isEmpty()) {
                JingsaiTuanduiChengyuanEntity newLeader = newMembers.get(0);
                newLeader.setJuese("负责人");
                chengyuanService.updateById(newLeader);
            }

            // 更新团队负责人字段
            JingsaiTuanduiEntity tuandui = tuanduiDao.selectById(tuanduiId);
            if (tuandui != null) {
                tuandui.setFuzerenXuehao(newData.getString("xuehao"));
                tuanduiDao.updateById(tuandui);
            }
        }

        log.info("执行人员变更成功，团队ID：{}，变更类型：{}", tuanduiId, bianguengType);
    }

    /**
     * 更新团队人数
     */
    private void updateTuanduiRenshu(Long tuanduiId, int delta) {
        JingsaiTuanduiEntity tuandui = tuanduiDao.selectById(tuanduiId);
        if (tuandui != null) {
            int currentCount = tuandui.getChengyuanRenshu() != null ? tuandui.getChengyuanRenshu() : 0;
            tuandui.setChengyuanRenshu(currentCount + delta);
            tuanduiDao.updateById(tuandui);
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JingsaiRenyuanBianguengServiceImpl.class);
}
