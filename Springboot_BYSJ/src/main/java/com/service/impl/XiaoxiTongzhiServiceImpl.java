package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.XiaoxiTongzhiDao;
import com.entity.XiaoxiTongzhiEntity;
import com.entity.view.XiaoxiTongzhiView;
import com.service.EmailService;
import com.service.XiaoxiTongzhiService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 消息通知 Service 实现类
 */
@Slf4j
@Service("xiaoxiTongzhiService")
public class XiaoxiTongzhiServiceImpl extends ServiceImpl<XiaoxiTongzhiDao, XiaoxiTongzhiEntity> implements XiaoxiTongzhiService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.service.XueshengService xueshengService;

    @Autowired
    private com.service.JiaoshiService jiaoshiService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<XiaoxiTongzhiEntity> page = this.selectPage(
                new Query<XiaoxiTongzhiEntity>(params).getPage(),
                new EntityWrapper<XiaoxiTongzhiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageView(Map<String, Object> params) {
        Page<XiaoxiTongzhiView> page = new Query<XiaoxiTongzhiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, params));
        return new PageUtils(page);
    }

    @Override
    public void sendTongzhi(String biaoti, String neirong, String leixing, String fasongren,
                            String jieshourenXuehao, String jieshourenGonghao, String jieshourenJuese,
                            Long guanlianId, String guanlianLeixing) {
        try {
            XiaoxiTongzhiEntity entity = new XiaoxiTongzhiEntity();
            entity.setBiaoti(biaoti);
            entity.setNeirong(neirong);
            entity.setLeixing(leixing != null ? leixing : "系统通知");
            entity.setFasongren(fasongren != null ? fasongren : "系统");
            entity.setJieshourenXuehao(jieshourenXuehao);
            entity.setJieshourenGonghao(jieshourenGonghao);
            entity.setJieshourenJuese(jieshourenJuese);
            entity.setGuanlianId(guanlianId);
            entity.setGuanlianLeixing(guanlianLeixing);
            entity.setIsRead("未读");
            entity.setAddtime(new Date());
            
            this.insert(entity);
            log.info("✓ 消息通知发送成功：标题={}, 接收人={}", biaoti, jieshourenXuehao != null ? jieshourenXuehao : jieshourenGonghao);

            // 【论文3.1.1(6)】邮件推送通知 - 同时通过邮件推送渠道发送通知
            try {
                String toEmail = null;
                if ("xuesheng".equals(jieshourenJuese) && jieshourenXuehao != null) {
                    // 查询学生邮箱
                    com.entity.XueshengEntity xuesheng = xueshengService.selectOne(
                        new EntityWrapper<com.entity.XueshengEntity>().eq("xuehao", jieshourenXuehao));
                    if (xuesheng != null && xuesheng.getYouxiang() != null && !xuesheng.getYouxiang().isEmpty()) {
                        toEmail = xuesheng.getYouxiang(); // 学生邮箱字段
                    }
                } else if ("jiaoshi".equals(jieshourenJuese) && jieshourenGonghao != null) {
                    // 查询教师邮箱
                    com.entity.JiaoshiEntity jiaoshi = jiaoshiService.selectOne(
                        new EntityWrapper<com.entity.JiaoshiEntity>().eq("gonghao", jieshourenGonghao));
                    if (jiaoshi != null && jiaoshi.getYouxiang() != null && !jiaoshi.getYouxiang().isEmpty()) {
                        toEmail = jiaoshi.getYouxiang(); // 教师邮箱字段
                    }
                }
                if (toEmail != null) {
                    String htmlContent = "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto'>"
                        + "<h2 style='color:#333;border-bottom:1px solid #eee;padding-bottom:10px'>" + biaoti + "</h2>"
                        + "<p style='color:#666;line-height:1.8'>" + neirong + "</p>"
                        + "<p style='color:#999;font-size:12px;margin-top:20px'>此邮件由大学生竞赛管理系统自动发送</p>"
                        + "</div>";
                    boolean emailSent = emailService.sendEmail(toEmail, "[竞赛管理系统] " + biaoti, htmlContent);
                    if (emailSent) {
                        log.info("✓ 邮件推送成功：收件人={}, 主题={}", toEmail, biaoti);
                    }
                }
            } catch (Exception e) {
                log.warn("邮件推送异常（不影响站内消息）：{}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("✗ 消息通知发送失败：标题={}, 错误={}", biaoti, e.getMessage(), e);
        }
    }
}
