package com.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dao.XiaoxiTongzhiDao;
import com.entity.XiaoxiTongzhiEntity;
import com.entity.view.XiaoxiTongzhiView;
import com.service.XiaoxiTongzhiService;
import com.utils.PageUtils;
import com.utils.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 消息通知 Service 实现类
 */
@Slf4j
@Service("xiaoxiTongzhiService")
public class XiaoxiTongzhiServiceImpl extends ServiceImpl<XiaoxiTongzhiDao, XiaoxiTongzhiEntity> implements XiaoxiTongzhiService {

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
        } catch (Exception e) {
            log.error("✗ 消息通知发送失败：标题={}, 错误={}", biaoti, e.getMessage(), e);
        }
    }
}
