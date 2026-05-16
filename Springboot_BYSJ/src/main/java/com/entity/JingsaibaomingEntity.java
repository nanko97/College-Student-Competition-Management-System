package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 竞赛报名
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
@TableName("jingsaibaoming")
public class JingsaibaomingEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;


    public JingsaibaomingEntity() {

    }

    public JingsaibaomingEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 主键id
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 工号
     */

    private String gonghao;

    /**
     * 教师姓名
     */

    private String jiaoshixingming;

    /**
     * 竞赛名称
     */

    private String jingsaimingcheng;

    /**
     * 竞赛类型（非数据库字段，通过关联查询填充）
     */
    @TableField(exist = false)
    private String jingsaileixing;

    /**
     * 赛道ID
     */

    private Long saidaoId;

    /**
     * 赛道名称（非数据库字段，通过关联查询填充）
     */
    @TableField(exist = false)
    private String saidaoMingcheng;

    /**
     * 团队ID
     */

    private Long tuanduiId;

    /**
     * 团队编号
     */

    private String tuanduiBianhao;

    /**
     * 参赛类型
     */
    
    private String cansaileixing;

    /**
     * 参赛人员
     */

    private String cansairenyuan;

    /**
     * 参赛作品
     */

    private String cansaizuopin;

    /**
     * 参赛宣言
     */

    private String cansaixuanyan;

    /**
     * 申请日期
     */

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat
    private Date shenqingriqi;

    /**
     * 学号
     */

    private String xuehao;

    /**
     * 学生姓名
     */

    private String xueshengxingming;

    /**
     * 是否审核
     */

    private String sfsh;

    /**
     * 审核回复
     */

    private String shhf;

    /**
     * 是否支付
     */

    private String ispay;

    /**
     * 晋级级别(校级/省级/国家级)
     */

    private String jinjiJibie;

    /**
     * 原报名ID(如果是晋级报名)
     */

    private Long yuanBaomingId;

    /**
     * 竞赛ID
     */

    private Long jingsaiId;


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 设置：工号
     */
    public void setGonghao(String gonghao) {
        this.gonghao = gonghao;
    }

    /**
     * 获取：工号
     */
    public String getGonghao() {
        return gonghao;
    }

    /**
     * 设置：教师姓名
     */
    public void setJiaoshixingming(String jiaoshixingming) {
        this.jiaoshixingming = jiaoshixingming;
    }

    /**
     * 获取：教师姓名
     */
    public String getJiaoshixingming() {
        return jiaoshixingming;
    }

    /**
     * 设置：竞赛名称
     */
    public void setJingsaimingcheng(String jingsaimingcheng) {
        this.jingsaimingcheng = jingsaimingcheng;
    }

    /**
     * 获取：竞赛名称
     */
    public String getJingsaimingcheng() {
        return jingsaimingcheng;
    }

    /**
     * 设置：竞赛类型
     */
    public void setJingsaileixing(String jingsaileixing) {
        this.jingsaileixing = jingsaileixing;
    }

    /**
     * 获取：竞赛类型
     */
    public String getJingsaileixing() {
        return jingsaileixing;
    }

    /**
     * 设置：参赛类型
     */
    public void setCansaileixing(String cansaileixing) {
        this.cansaileixing = cansaileixing;
    }

    /**
     * 获取：参赛类型
     */
    public String getCansaileixing() {
        return cansaileixing;
    }

    /**
     * 设置：参赛人员
     */
    public void setCansairenyuan(String cansairenyuan) {
        this.cansairenyuan = cansairenyuan;
    }

    /**
     * 获取：参赛人员
     */
    public String getCansairenyuan() {
        return cansairenyuan;
    }

    /**
     * 设置：参赛作品
     */
    public void setCansaizuopin(String cansaizuopin) {
        this.cansaizuopin = cansaizuopin;
    }

    /**
     * 获取：参赛作品
     */
    public String getCansaizuopin() {
        return cansaizuopin;
    }

    /**
     * 设置：参赛宣言
     */
    public void setCansaixuanyan(String cansaixuanyan) {
        this.cansaixuanyan = cansaixuanyan;
    }

    /**
     * 获取：参赛宣言
     */
    public String getCansaixuanyan() {
        return cansaixuanyan;
    }

    /**
     * 设置：申请日期
     */
    public void setShenqingriqi(Date shenqingriqi) {
        this.shenqingriqi = shenqingriqi;
    }

    /**
     * 获取：申请日期
     */
    public Date getShenqingriqi() {
        return shenqingriqi;
    }

    /**
     * 设置：学号
     */
    public void setXuehao(String xuehao) {
        this.xuehao = xuehao;
    }

    /**
     * 获取：学号
     */
    public String getXuehao() {
        return xuehao;
    }

    /**
     * 设置：学生姓名
     */
    public void setXueshengxingming(String xueshengxingming) {
        this.xueshengxingming = xueshengxingming;
    }

    /**
     * 获取：学生姓名
     */
    public String getXueshengxingming() {
        return xueshengxingming;
    }

    /**
     * 设置：是否审核
     */
    public void setSfsh(String sfsh) {
        this.sfsh = sfsh;
    }

    /**
     * 获取：是否审核
     */
    public String getSfsh() {
        return sfsh;
    }

    /**
     * 设置：审核回复
     */
    public void setShhf(String shhf) {
        this.shhf = shhf;
    }

    /**
     * 获取：审核回复
     */
    public String getShhf() {
        return shhf;
    }

    /**
     * 设置：是否支付
     */
    public void setIspay(String ispay) {
        this.ispay = ispay;
    }

    /**
     * 获取：是否支付
     */
    public String getIspay() {
        return ispay;
    }

    /**
     * 设置：赛道ID
     */
    public void setSaidaoId(Long saidaoId) {
        this.saidaoId = saidaoId;
    }

    /**
     * 获取：赛道ID
     */
    public Long getSaidaoId() {
        return saidaoId;
    }

    /**
     * 设置：赛道名称
     */
    public void setSaidaoMingcheng(String saidaoMingcheng) {
        this.saidaoMingcheng = saidaoMingcheng;
    }

    /**
     * 获取：赛道名称
     */
    public String getSaidaoMingcheng() {
        return saidaoMingcheng;
    }

    /**
     * 设置：团队ID
     */
    public void setTuanduiId(Long tuanduiId) {
        this.tuanduiId = tuanduiId;
    }

    /**
     * 获取：团队ID
     */
    public Long getTuanduiId() {
        return tuanduiId;
    }

    /**
     * 设置：团队编号
     */
    public void setTuanduiBianhao(String tuanduiBianhao) {
        this.tuanduiBianhao = tuanduiBianhao;
    }

    /**
     * 获取：团队编号
     */
    public String getTuanduiBianhao() {
        return tuanduiBianhao;
    }

    /**
     * 设置：晋级级别
     */
    public void setJinjiJibie(String jinjiJibie) {
        this.jinjiJibie = jinjiJibie;
    }

    /**
     * 获取：晋级级别
     */
    public String getJinjiJibie() {
        return jinjiJibie;
    }

    /**
     * 设置：原报名ID
     */
    public void setYuanBaomingId(Long yuanBaomingId) {
        this.yuanBaomingId = yuanBaomingId;
    }

    /**
     * 获取：原报名ID
     */
    public Long getYuanBaomingId() {
        return yuanBaomingId;
    }

    /**
     * 设置：竞赛ID
     */
    public void setJingsaiId(Long jingsaiId) {
        this.jingsaiId = jingsaiId;
    }

    /**
     * 获取：竞赛ID
     */
    public Long getJingsaiId() {
        return jingsaiId;
    }

}
