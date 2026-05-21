package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiTuanduiDao;
import com.entity.JingsaiSaidaoEntity;
import com.entity.JingsaiTuanduiChengyuanEntity;
import com.entity.JingsaiTuanduiEntity;
import com.entity.XueshengEntity;
import com.service.JingsaiSaidaoService;
import com.service.JingsaiTuanduiChengyuanService;
import com.service.JingsaiTuanduiService;
import com.service.XueshengService;
import com.utils.IdWorker;
import com.utils.PageUtils;
import com.utils.Query;
import com.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("jingsaiTuanduiService")
public class JingsaiTuanduiServiceImpl extends ServiceImpl<JingsaiTuanduiDao, JingsaiTuanduiEntity> implements JingsaiTuanduiService {

    @Autowired
    private JingsaiTuanduiChengyuanService chengyuanService;
    
    @Autowired
    private JingsaiSaidaoService saidaoService;
    
    @Autowired
    private XueshengService xueshengService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        EntityWrapper<JingsaiTuanduiEntity> ew = new EntityWrapper<>();
        
        // 处理搜索条件
        String tuanduiMingcheng = (String) params.get("tuanduiMingcheng");
        if (tuanduiMingcheng != null && !tuanduiMingcheng.isEmpty()) {
            ew.like("tuandui_mingcheng", tuanduiMingcheng);
        }
        
        String jingsaimingcheng = (String) params.get("jingsaimingcheng");
        if (jingsaimingcheng != null && !jingsaimingcheng.isEmpty()) {
            ew.like("jingsaimingcheng", jingsaimingcheng);
        }
        
        String shenheZhuangtai = (String) params.get("shenheZhuangtai");
        if (shenheZhuangtai != null && !shenheZhuangtai.isEmpty()) {
            ew.eq("shenhe_zhuangtai", shenheZhuangtai);
        }
        
        Page<JingsaiTuanduiEntity> page = this.selectPage(new Query<JingsaiTuanduiEntity>(params).getPage(), ew);
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<JingsaiTuanduiEntity> wrapper) {
        // 如果wrapper为null，创建一个新的
        if (wrapper == null) {
            wrapper = new EntityWrapper<>();
        }
        
        // 在现有wrapper基础上，添加搜索条件
        String tuanduiMingcheng = (String) params.get("tuanduiMingcheng");
        if (tuanduiMingcheng != null && !tuanduiMingcheng.isEmpty()) {
            wrapper.like("tuandui_mingcheng", tuanduiMingcheng);
        }
        
        String jingsaimingcheng = (String) params.get("jingsaimingcheng");
        if (jingsaimingcheng != null && !jingsaimingcheng.isEmpty()) {
            wrapper.like("jingsaimingcheng", jingsaimingcheng);
        }
        
        String shenheZhuangtai = (String) params.get("shenheZhuangtai");
        if (shenheZhuangtai != null && !shenheZhuangtai.isEmpty()) {
            wrapper.eq("shenhe_zhuangtai", shenheZhuangtai);
        }
        
        Page<JingsaiTuanduiEntity> page = new Query<JingsaiTuanduiEntity>(params).getPage();
        List<JingsaiTuanduiEntity> records = baseMapper.selectListView((com.baomidou.mybatisplus.mapper.Wrapper<JingsaiTuanduiEntity>) wrapper);
        page.setTotal((long) records.size());
        page.setRecords(records);
        return new PageUtils(page);
    }

    @Override
    public JingsaiTuanduiEntity getByTuanduiBianhao(String tuanduiBianhao) {
        return baseMapper.getByTuanduiBianhao(tuanduiBianhao);
    }

    /**
     * 创建团队
     * 业务规则：
     * 1. 团队编号自动生成，格式为T+时间戳（如T202505161430）
     * 2. 创建者自动添加为第一个成员，角色为"负责人"
     * 3. 唯一性校验：同一学生在同一竞赛中只能属于一个团队
     * 4. 人数上限校验：不能超过赛道设定的tuandui_renshu_max
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R createTuandui(JingsaiTuanduiEntity tuandui, String caozuoRen) {
        try {
            log.info("========== 创建团队开始 ==========");
            log.info("操作人: {}", caozuoRen);
            log.info("接收到的团队数据: {}", tuandui);
            log.info("jingsaiId: {}, 类型: {}", tuandui.getJingsaiId(), tuandui.getJingsaiId() != null ? tuandui.getJingsaiId().getClass().getName() : "null");
            log.info("saidaoId: {}, 类型: {}", tuandui.getSaidaoId(), tuandui.getSaidaoId() != null ? tuandui.getSaidaoId().getClass().getName() : "null");
            log.info("tuanduiMingcheng: {}", tuandui.getTuanduiMingcheng());
            log.info("fuzerenXuehao: {}", tuandui.getFuzerenXuehao());
            log.info("fuzerenXingming: {}", tuandui.getFuzerenXingming());
            
            // ========== 1. 参数校验 ==========
            if (tuandui.getJingsaiId() == null) {
                log.error("参数校验失败: jingsaiId为空");
                return R.error("请选择竞赛");
            }
            if (tuandui.getSaidaoId() == null) {
                log.error("参数校验失败: saidaoId为空");
                return R.error("请选择赛道");
            }
            if (tuandui.getFuzerenXuehao() == null || tuandui.getFuzerenXuehao().isEmpty()) {
                return R.error("负责人学号不能为空");
            }
            
            // ========== 1.5 兜底处理：如果负责人姓名为空，从学生表查询填充 ==========
            if (tuandui.getFuzerenXingming() == null || tuandui.getFuzerenXingming().isEmpty()) {
                log.warn("负责人姓名为空，从学生表查询 - 学号: {}", tuandui.getFuzerenXuehao());
                EntityWrapper<XueshengEntity> xueshengEw = new EntityWrapper<>();
                xueshengEw.eq("xuehao", tuandui.getFuzerenXuehao());
                XueshengEntity xuesheng = xueshengService.selectOne(xueshengEw);
                if (xuesheng != null && xuesheng.getXueshengxingming() != null) {
                    tuandui.setFuzerenXingming(xuesheng.getXueshengxingming());
                    log.info("从学生表查询到姓名: {}", tuandui.getFuzerenXingming());
                } else {
                    log.error("未找到学生信息，学号: {}", tuandui.getFuzerenXuehao());
                    return R.error("未找到学生信息，学号: " + tuandui.getFuzerenXuehao());
                }
            }
            
            // ========== 2. 唯一性校验：同一学生在同一竞赛中只能属于一个团队 ==========
            // 2.1 检查是否已经是其他团队的负责人
            EntityWrapper<JingsaiTuanduiEntity> leaderEw = new EntityWrapper<>();
            leaderEw.eq("fuzeren_xuehao", tuandui.getFuzerenXuehao());
            leaderEw.eq("jingsai_id", tuandui.getJingsaiId());
            List<JingsaiTuanduiEntity> existingLeaderTeams = this.selectList(leaderEw);
            
            if (!existingLeaderTeams.isEmpty()) {
                log.warn("学生 {} 已经是竞赛 {} 中团队 {} 的负责人", 
                    tuandui.getFuzerenXuehao(), tuandui.getJingsaiId(), existingLeaderTeams.get(0).getTuanduiMingcheng());
                return R.error("您已经是该竞赛中某个团队的负责人，不能重复创建");
            }
            
            // 2.2 检查是否已经是其他团队的成员
            EntityWrapper<JingsaiTuanduiChengyuanEntity> memberEw = new EntityWrapper<>();
            memberEw.eq("xuehao", tuandui.getFuzerenXuehao());
            memberEw.eq("is_active", "是");
            List<JingsaiTuanduiChengyuanEntity> existingMemberships = chengyuanService.selectList(memberEw);
            
            if (!existingMemberships.isEmpty()) {
                // 检查这些成员是否属于同一个竞赛
                for (JingsaiTuanduiChengyuanEntity membership : existingMemberships) {
                    JingsaiTuanduiEntity existingTeam = this.selectById(membership.getTuanduiId());
                    if (existingTeam != null && existingTeam.getJingsaiId().equals(tuandui.getJingsaiId())) {
                        log.warn("学生 {} 已经是竞赛 {} 中团队 {} 的成员", 
                            tuandui.getFuzerenXuehao(), tuandui.getJingsaiId(), existingTeam.getTuanduiMingcheng());
                        return R.error("您已经是该竞赛中某个团队的成员，不能重复加入");
                    }
                }
            }
            
            // ========== 3. 人数上限校验 ==========
            JingsaiSaidaoEntity saidao = saidaoService.selectById(tuandui.getSaidaoId());
            if (saidao == null) {
                return R.error("赛道不存在");
            }
            
            Integer maxMembers = saidao.getTuanduiRenshuMax();
            if (maxMembers != null && maxMembers < 1) {
                return R.error("赛道设置的人数上限无效");
            }
            
            // 初始人数为1（负责人），检查是否超过上限
            if (maxMembers != null && 1 > maxMembers) {
                return R.error("赛道要求最少" + saidao.getTuanduiRenshuMin() + "人，当前设置不符合要求");
            }
            
            log.info("人数校验通过 - 赛道最大人数:{}, 初始人数:1", maxMembers);
            
            // ========== 4. 生成团队编号 ==========
            // 格式：T + 年月日时分秒（如T20250516143059）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date());
            String bianhao = "T" + timestamp;
            
            // 检查编号唯一性（理论上不会重复，但为了安全）
            JingsaiTuanduiEntity existingTeam = this.getByTuanduiBianhao(bianhao);
            if (existingTeam != null) {
                // 如果重复，添加随机数
                bianhao = "T" + timestamp + IdWorker.getId() % 1000;
            }
            
            tuandui.setTuanduiBianhao(bianhao);
            log.info("生成团队编号: {}", bianhao);
            
            // ========== 5. 设置默认值 ==========
            tuandui.setChengyuanRenshu(1);  // 初始人数为1（负责人）
            tuandui.setShenheZhuangtai("待审核");
            
            // ========== 6. 保存团队 ==========
            tuandui.setId(IdWorker.getId());
            this.insert(tuandui);
            log.info("团队保存成功 - ID:{}, 编号:{}", tuandui.getId(), bianhao);
            
            // ========== 7. 自动添加负责人为第一个成员 ==========
            JingsaiTuanduiChengyuanEntity chengyuan = new JingsaiTuanduiChengyuanEntity();
            chengyuan.setId(IdWorker.getId());
            chengyuan.setTuanduiId(tuandui.getId());
            chengyuan.setTuanduiBianhao(bianhao);
            chengyuan.setXuehao(tuandui.getFuzerenXuehao());
            chengyuan.setXueshengxingming(tuandui.getFuzerenXingming());
            chengyuan.setJuese("负责人");  // 角色设置为"负责人"
            chengyuan.setIsActive("是");
            chengyuan.setRuzuiShijian(new Date());
            chengyuanService.insert(chengyuan);
            log.info("负责人添加为团队成员成功 - 学号:{}", tuandui.getFuzerenXuehao());
            
            log.info("团队创建成功 - 团队ID:{}, 编号:{}, 名称:{}", 
                tuandui.getId(), bianhao, tuandui.getTuanduiMingcheng());
            
            return R.ok("团队创建成功，团队编号：" + bianhao);
            
        } catch (Exception e) {
            log.error("创建团队失败:", e);
            throw new RuntimeException("创建团队失败：" + e.getMessage());
        }
    }
}
