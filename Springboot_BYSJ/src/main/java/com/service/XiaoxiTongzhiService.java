package com.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.entity.XiaoxiTongzhiEntity;
import com.entity.view.XiaoxiTongzhiView;
import com.utils.PageUtils;

import java.util.Map;

/**
 * 消息通知 Service 接口
 */
public interface XiaoxiTongzhiService extends IService<XiaoxiTongzhiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    PageUtils queryPageView(Map<String, Object> params);
    
    /**
     * 发送消息通知
     * @param biaoti 标题
     * @param neirong 内容
     * @param leixing 类型
     * @param fasongren 发送人
     * @param jieshourenXuehao 接收人学号
     * @param jieshourenGonghao 接收人工号
     * @param jieshourenJuese 接收人角色
     * @param guanlianId 关联业务ID
     * @param guanlianLeixing 关联业务类型
     */
    void sendTongzhi(String biaoti, String neirong, String leixing, String fasongren, 
                     String jieshourenXuehao, String jieshourenGonghao, String jieshourenJuese,
                     Long guanlianId, String guanlianLeixing);
}
