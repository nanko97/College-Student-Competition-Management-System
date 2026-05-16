package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 作品打分复核申请
 * 数据库通用操作实体类（普通增删改查）
 */
@TableName("zuopindafen_fuhe")
public class ZuopindafenFuheEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 作品打分记录ID
     */
    private Long zuopindafenId;

    /**
     * 竞赛名称
     */
    private String jingsaimingcheng;

    /**
     * 原成绩
     */
    private String yuanChengji;

    /**
     * 学号
     */
    private String xuehao;

    /**
     * 学生姓名
     */
    private String xueshengxingming;

    /**
     * 复核理由
     */
    private String fuheReason;

    /**
     * 复核状态（待审核/已通过/已驳回）
     */
    private String fuheStatus;

    /**
     * 新成绩（教师重新打分）
     */
    private String xinChengji;

    /**
     * 审核意见
     */
    private String shenheYijian;

    /**
     * 审核教师工号
     */
    private String shenheGonghao;

    /**
     * 审核教师姓名
     */
    private String shenheJiaoshi;

    /**
     * 申请时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenqingShijian;

    /**
     * 审核时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenheShijian;

    /**
     * 设置：主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置：作品打分记录ID
     */
    public void setZuopindafenId(Long zuopindafenId) {
        this.zuopindafenId = zuopindafenId;
    }

    /**
     * 获取：作品打分记录ID
     */
    public Long getZuopindafenId() {
        return zuopindafenId;
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
     * 设置：原成绩
     */
    public void setYuanChengji(String yuanChengji) {
        this.yuanChengji = yuanChengji;
    }

    /**
     * 获取：原成绩
     */
    public String getYuanChengji() {
        return yuanChengji;
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
     * 设置：复核理由
     */
    public void setFuheReason(String fuheReason) {
        this.fuheReason = fuheReason;
    }

    /**
     * 获取：复核理由
     */
    public String getFuheReason() {
        return fuheReason;
    }

    /**
     * 设置：复核状态
     */
    public void setFuheStatus(String fuheStatus) {
        this.fuheStatus = fuheStatus;
    }

    /**
     * 获取：复核状态
     */
    public String getFuheStatus() {
        return fuheStatus;
    }

    /**
     * 设置：新成绩
     */
    public void setXinChengji(String xinChengji) {
        this.xinChengji = xinChengji;
    }

    /**
     * 获取：新成绩
     */
    public String getXinChengji() {
        return xinChengji;
    }

    /**
     * 设置：审核意见
     */
    public void setShenheYijian(String shenheYijian) {
        this.shenheYijian = shenheYijian;
    }

    /**
     * 获取：审核意见
     */
    public String getShenheYijian() {
        return shenheYijian;
    }

    /**
     * 设置：审核教师工号
     */
    public void setShenheGonghao(String shenheGonghao) {
        this.shenheGonghao = shenheGonghao;
    }

    /**
     * 获取：审核教师工号
     */
    public String getShenheGonghao() {
        return shenheGonghao;
    }

    /**
     * 设置：审核教师姓名
     */
    public void setShenheJiaoshi(String shenheJiaoshi) {
        this.shenheJiaoshi = shenheJiaoshi;
    }

    /**
     * 获取：审核教师姓名
     */
    public String getShenheJiaoshi() {
        return shenheJiaoshi;
    }

    /**
     * 设置：申请时间
     */
    public void setShenqingShijian(Date shenqingShijian) {
        this.shenqingShijian = shenqingShijian;
    }

    /**
     * 获取：申请时间
     */
    public Date getShenqingShijian() {
        return shenqingShijian;
    }

    /**
     * 设置：审核时间
     */
    public void setShenheShijian(Date shenheShijian) {
        this.shenheShijian = shenheShijian;
    }

    /**
     * 获取：审核时间
     */
    public Date getShenheShijian() {
        return shenheShijian;
    }
}
