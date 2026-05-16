package com.service;

import com.entity.JiaoshiEntity;
import com.entity.XueshengEntity;

/**
 * 数据联动同步服务
 * 功能：当学生/教师信息变更时，自动同步更新所有关联表中的冗余数据
 */
public interface DataSyncService {

    /**
     * 学生信息变更同步
     * 当学生在个人中心修改姓名、学院、班级、手机、邮箱时，
     * 自动更新以下表中的对应数据：
     * - jingsai_tuandui_chengyuan (团队成员表)
     * - jingsaibaoming (竞赛报名表)
     * - zuopindafen (作品打分表)
     * - zuopindafen_fuhe (作品复核表)
     * - jingsai_jiaofei_jilu (缴费记录表)
     * - tuandui_apply (团队申请表)
     * 
     * @param xuesheng 更新后的学生信息
     */
    void syncXueshengInfo(XueshengEntity xuesheng);

    /**
     * 教师信息变更同步
     * 当教师在个人中心修改姓名、学院、手机、邮箱时，
     * 自动更新以下表中的对应数据：
     * - jingsaibaoming (竞赛报名表)
     * - zuopindafen (作品打分表)
     * - jingsaixinxi (竞赛信息表)
     * - zuopindafen_fuhe (作品复核表)
     * 
     * @param jiaoshi 更新后的教师信息
     */
    void syncJiaoshiInfo(JiaoshiEntity jiaoshi);
}
