package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.JingsaiJinjiJiluDao;
import com.entity.*;
import com.service.*;
import com.utils.IdWorker;
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

@Service("jingsaiJinjiJiluService")
@Slf4j
public class JingsaiJinjiJiluServiceImpl extends ServiceImpl<JingsaiJinjiJiluDao, JingsaiJinjiJiluEntity> implements JingsaiJinjiJiluService {

    @Autowired
    private JingsaiJinjiGuanxiService jinjiGuanxiService;

    @Autowired
    private JingsaibaomingService jingsaibaomingService;

    @Autowired
    private JingsaixinxiService jingsaixinxiService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 构建查询条件
        EntityWrapper<JingsaiJinjiJiluEntity> ew = new EntityWrapper<>();
        
        // 晋级状态精确匹配
        if (params.get("jinjiZhuangtai") != null && !params.get("jinjiZhuangtai").toString().isEmpty()) {
            ew.eq("jinji_zhuangtai", params.get("jinjiZhuangtai").toString());
            log.debug("筛选项：晋级状态 = {}", params.get("jinjiZhuangtai"));
        }
        
        Page<JingsaiJinjiJiluEntity> page = this.selectPage(
            new Query<JingsaiJinjiJiluEntity>(params).getPage(),
            ew
        );
        log.debug("晋级记录查询结果: 总数={}, 当前页={}", page.getTotal(), page.getCurrent());
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<JingsaiJinjiJiluEntity> wrapper) {
        // 使用 Query 获取分页参数
        Page<JingsaiJinjiJiluEntity> page = new Query<JingsaiJinjiJiluEntity>(params).getPage();
        
        // 查询总数
        Integer total = baseMapper.selectCount(wrapper);
        
        // 使用 selectListView 获取完整字段数据（包含别名映射）
        List<JingsaiJinjiJiluEntity> allRecords = baseMapper.selectListView(wrapper);
        
        // 手动分页：计算起始和结束索引
        int currPage = page.getCurrent();
        int pageSize = page.getSize();
        int fromIndex = (currPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allRecords.size());
        
        // 提取当前页数据
        List<JingsaiJinjiJiluEntity> pageRecords;
        if (fromIndex < allRecords.size()) {
            pageRecords = allRecords.subList(fromIndex, toIndex);
        } else {
            pageRecords = new java.util.ArrayList<>();
        }
        
        // 设置分页信息
        page.setTotal(total != null ? total.longValue() : 0L);
        page.setRecords(pageRecords);
        
        return new PageUtils(page);
    }

    /**
     * 发起晋级
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R initiateJinji(Long baomingId, Long xinJingsaiId, String jinjiYuanyin, String caozuoRen) {
        try {
            // 1. 查询原报名记录
            JingsaibaomingEntity yuanBaoming = jingsaibaomingService.selectById(baomingId);
            if (yuanBaoming == null) {
                return R.error("报名记录不存在");
            }

            // 2. 查询新竞赛信息
            JingsaixinxiEntity xinJingsai = jingsaixinxiService.selectById(xinJingsaiId);
            if (xinJingsai == null) {
                return R.error("目标竞赛不存在");
            }

            // 3. 查询晋级关系
            JingsaiJinjiGuanxiEntity guanxi = jinjiGuanxiService.getByFuZiJingsai(
                yuanBaoming.getJingsaiId() != null ? yuanBaoming.getJingsaiId() : 
                findJingsaiIdByName(yuanBaoming.getJingsaimingcheng()), 
                xinJingsaiId);
            
            if (guanxi == null || "否".equals(guanxi.getIsActive())) {
                return R.error("未配置晋级关系或关系已禁用");
            }

            // 4. 验证晋级资格（如果报名表中有分数字段则启用）
            // 注：当前jingsaibaoming表暂无zuopindafen和mingci字段
            // 如需启用此功能，请先在数据库中添加对应字段
            /*
            if (guanxi.getZuidiFenshu() != null && yuanBaoming.getZuopindafen() != null) {
                if (yuanBaoming.getZuopindafen().compareTo(new java.math.BigDecimal(guanxi.getZuidiFenshu())) < 0) {
                    return R.error("分数不足，无法晋级。要求最低分数：" + guanxi.getZuidiFenshu() + 
                        "，当前分数：" + yuanBaoming.getZuopindafen());
                }
            }
            
            if (guanxi.getZuidiMingci() != null && yuanBaoming.getMingci() != null) {
                if (yuanBaoming.getMingci() > guanxi.getZuidiMingci()) {
                    return R.error("名次不足，无法晋级。要求最高名次：" + guanxi.getZuidiMingci() + 
                        "，当前名次：" + yuanBaoming.getMingci());
                }
            }
            */

            // 5. 验证是否已晋级
            EntityWrapper<JingsaiJinjiJiluEntity> ew = new EntityWrapper<>();
            ew.eq("yuan_baoming_id", baomingId);
            ew.eq("xin_jingsai_id", xinJingsaiId);
            Integer count = this.selectCount(ew);
            if (count > 0) {
                return R.error("该报名已发起过晋级");
            }

            // 6. 创建新的报名记录
            JingsaibaomingEntity xinBaoming = new JingsaibaomingEntity();
            xinBaoming.setId(IdWorker.getId());
            xinBaoming.setJingsaiId(xinJingsaiId);
            xinBaoming.setJingsaimingcheng(xinJingsai.getJingsaimingcheng());
            xinBaoming.setJingsaileixing(xinJingsai.getJingsaileixing());
            xinBaoming.setXuehao(yuanBaoming.getXuehao());
            xinBaoming.setXueshengxingming(yuanBaoming.getXueshengxingming());
            xinBaoming.setCansaileixing(yuanBaoming.getCansaileixing());
            xinBaoming.setCansairenyuan(yuanBaoming.getCansairenyuan());
            xinBaoming.setCansaizuopin(yuanBaoming.getCansaizuopin());
            xinBaoming.setCansaixuanyan(yuanBaoming.getCansaixuanyan());
            xinBaoming.setYuanBaomingId(baomingId); // 关联原报名
            xinBaoming.setJinjiJibie(guanxi.getZiJibie());
            xinBaoming.setSfsh("否"); // 待审核
            xinBaoming.setIspay("未支付");
            xinBaoming.setShenqingriqi(new Date());
            
            // 如果是团队赛，复制团队信息
            if (yuanBaoming.getTuanduiId() != null) {
                xinBaoming.setTuanduiId(yuanBaoming.getTuanduiId());
                xinBaoming.setTuanduiBianhao(yuanBaoming.getTuanduiBianhao());
            }
            
            jingsaibaomingService.insert(xinBaoming);

            // 7. 创建晋级记录
            JingsaiJinjiJiluEntity jinjiJilu = new JingsaiJinjiJiluEntity();
            jinjiJilu.setId(IdWorker.getId());
            jinjiJilu.setYuanBaomingId(baomingId);
            jinjiJilu.setXinBaomingId(xinBaoming.getId());
            jinjiJilu.setXuehao(yuanBaoming.getXuehao());
            jinjiJilu.setXueshengxingming(yuanBaoming.getXueshengxingming());
            jinjiJilu.setYuanJingsaiId(yuanBaoming.getJingsaiId() != null ? yuanBaoming.getJingsaiId() : findJingsaiIdByName(yuanBaoming.getJingsaimingcheng()));
            jinjiJilu.setYuanJingsaimingcheng(yuanBaoming.getJingsaimingcheng());
            jinjiJilu.setYuanJibie(guanxi.getFuJibie());
            jinjiJilu.setXinJingsaiId(xinJingsaiId);
            jinjiJilu.setXinJingsaimingcheng(xinJingsai.getJingsaimingcheng());
            jinjiJilu.setXinJibie(guanxi.getZiJibie());
            jinjiJilu.setJinjiJibie(guanxi.getFuJibie() + "→" + guanxi.getZiJibie());
            jinjiJilu.setJinjiYuanyin(jinjiYuanyin);
            jinjiJilu.setJinjiZhuangtai("待审核");
            this.insert(jinjiJilu);

            return R.ok("晋级申请已提交，等待审核");
        } catch (Exception e) {
            throw new RuntimeException("发起晋级失败：" + e.getMessage());
        }
    }

    /**
     * 批量发起晋级
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R batchInitiateJinji(List<Long> baomingIds, Long xinJingsaiId, String jinjiYuanyin, String caozuoRen) {
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMsg = new StringBuilder();

        for (Long baomingId : baomingIds) {
            try {
                R result = initiateJinji(baomingId, xinJingsaiId, jinjiYuanyin, caozuoRen);
                if ((Integer) result.get("code") == 0) {
                    successCount++;
                } else {
                    failCount++;
                    failMsg.append("报名ID ").append(baomingId).append(": ").append(result.get("msg")).append("; ");
                }
            } catch (Exception e) {
                failCount++;
                failMsg.append("报名ID ").append(baomingId).append(": ").append(e.getMessage()).append("; ");
            }
        }

        Map<String, Object> data = new java.util.HashMap<>();
        data.put("successCount", successCount);
        data.put("failCount", failCount);
        data.put("failMsg", failMsg.toString());

        if (failCount > 0) {
            return R.ok("批量晋级完成，成功" + successCount + "个，失败" + failCount + "个").put("data", data);
        } else {
            return R.ok("批量晋级全部成功，共" + successCount + "个").put("data", data);
        }
    }

    /**
     * 审核晋级
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R shenheJinji(Long jinjiId, String zhuangtai, String shenheRen) {
        try {
            JingsaiJinjiJiluEntity jinji = this.selectById(jinjiId);
            if (jinji == null) {
                return R.error("晋级记录不存在");
            }

            // 幂等保护：防止重复审核
            if (!"待审核".equals(jinji.getJinjiZhuangtai())) {
                return R.error("该晋级申请已审核，请勿重复操作");
            }

            // 更新晋级记录
            jinji.setJinjiZhuangtai(zhuangtai);
            jinji.setShenheRen(shenheRen);
            jinji.setShenheShijian(new Date());
            this.updateById(jinji);

            // 如果通过，更新新报名的审核状态
            if ("已通过".equals(zhuangtai)) {
                JingsaibaomingEntity xinBaoming = jingsaibaomingService.selectById(jinji.getXinBaomingId());
                if (xinBaoming != null) {
                    xinBaoming.setSfsh("是");
                    xinBaoming.setShhf("晋级审核通过");
                    jingsaibaomingService.updateById(xinBaoming);
                }
            }

            return R.ok("审核完成");
        } catch (Exception e) {
            throw new RuntimeException("审核晋级失败：" + e.getMessage());
        }
    }

    /**
     * 根据竞赛名称查找ID（辅助方法）
     */
    private Long findJingsaiIdByName(String jingsaimingcheng) {
        if (jingsaimingcheng == null) return null;
        EntityWrapper<JingsaixinxiEntity> ew = new EntityWrapper<>();
        ew.eq("jingsaimingcheng", jingsaimingcheng);
        JingsaixinxiEntity jingsai = jingsaixinxiService.selectOne(ew);
        return jingsai != null ? jingsai.getId() : null;
    }
}
