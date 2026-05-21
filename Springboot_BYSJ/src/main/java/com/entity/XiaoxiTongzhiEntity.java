package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息通知实体类
 * 表名：xiaoxi_tongzhi
 */
@TableName("xiaoxi_tongzhi")
public class XiaoxiTongzhiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 消息标题
     */
    private String biaoti;

    /**
     * 消息内容
     */
    private String neirong;

    /**
     * 消息类型（系统通知/审核结果/成绩通知/截止提醒等）
     */
    private String leixing;

    /**
     * 发送人
     */
    private String fasongren;

    /**
     * 接收人学号（学生）
     */
    private String jieshourenXuehao;

    /**
     * 接收人工号（教师）
     */
    private String jieshourenGonghao;

    /**
     * 接收人角色（xuesheng/jiaoshi/admin）
     */
    private String jieshourenJuese;

    /**
     * 关联业务ID（如报名ID、竞赛ID等）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long guanlianId;

    /**
     * 关联业务类型（baoming/jingsai/pingfen等）
     */
    private String guanlianLeixing;

    /**
     * 是否已读（已读/未读）
     */
    private String isRead;

    /**
     * 阅读时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addtime;

    // ==================== Getter and Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBiaoti() {
        return biaoti;
    }

    public void setBiaoti(String biaoti) {
        this.biaoti = biaoti;
    }

    public String getNeirong() {
        return neirong;
    }

    public void setNeirong(String neirong) {
        this.neirong = neirong;
    }

    public String getLeixing() {
        return leixing;
    }

    public void setLeixing(String leixing) {
        this.leixing = leixing;
    }

    public String getFasongren() {
        return fasongren;
    }

    public void setFasongren(String fasongren) {
        this.fasongren = fasongren;
    }

    public String getJieshourenXuehao() {
        return jieshourenXuehao;
    }

    public void setJieshourenXuehao(String jieshourenXuehao) {
        this.jieshourenXuehao = jieshourenXuehao;
    }

    public String getJieshourenGonghao() {
        return jieshourenGonghao;
    }

    public void setJieshourenGonghao(String jieshourenGonghao) {
        this.jieshourenGonghao = jieshourenGonghao;
    }

    public String getJieshourenJuese() {
        return jieshourenJuese;
    }

    public void setJieshourenJuese(String jieshourenJuese) {
        this.jieshourenJuese = jieshourenJuese;
    }

    public Long getGuanlianId() {
        return guanlianId;
    }

    public void setGuanlianId(Long guanlianId) {
        this.guanlianId = guanlianId;
    }

    public String getGuanlianLeixing() {
        return guanlianLeixing;
    }

    public void setGuanlianLeixing(String guanlianLeixing) {
        this.guanlianLeixing = guanlianLeixing;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}
