package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛级别版本实体
 * 数据库表：jingsai_jibie_banben
 */
@TableName("jingsai_jibie_banben")
public class JingsaiJibieBanbenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private Long jingsaiId;
    private String jingsaimingcheng;
    private Integer jingsaiNianfen;
    private String jibie;
    private String renzhengJigou;
    private String wenjianHao;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat
    private Date shengxiaoRiqi;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat
    private Date shixiaoRiqi;

    private String beizhu;
    private String caozuoRen;
    private String isCurrent;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJingsaiId() { return jingsaiId; }
    public void setJingsaiId(Long jingsaiId) { this.jingsaiId = jingsaiId; }
    public String getJingsaimingcheng() { return jingsaimingcheng; }
    public void setJingsaimingcheng(String jingsaimingcheng) { this.jingsaimingcheng = jingsaimingcheng; }
    public Integer getJingsaiNianfen() { return jingsaiNianfen; }
    public void setJingsaiNianfen(Integer jingsaiNianfen) { this.jingsaiNianfen = jingsaiNianfen; }
    public String getJibie() { return jibie; }
    public void setJibie(String jibie) { this.jibie = jibie; }
    public String getRenzhengJigou() { return renzhengJigou; }
    public void setRenzhengJigou(String renzhengJigou) { this.renzhengJigou = renzhengJigou; }
    public String getWenjianHao() { return wenjianHao; }
    public void setWenjianHao(String wenjianHao) { this.wenjianHao = wenjianHao; }
    public Date getShengxiaoRiqi() { return shengxiaoRiqi; }
    public void setShengxiaoRiqi(Date shengxiaoRiqi) { this.shengxiaoRiqi = shengxiaoRiqi; }
    public Date getShixiaoRiqi() { return shixiaoRiqi; }
    public void setShixiaoRiqi(Date shixiaoRiqi) { this.shixiaoRiqi = shixiaoRiqi; }
    public String getBeizhu() { return beizhu; }
    public void setBeizhu(String beizhu) { this.beizhu = beizhu; }
    public String getCaozuoRen() { return caozuoRen; }
    public void setCaozuoRen(String caozuoRen) { this.caozuoRen = caozuoRen; }
    public String getIsCurrent() { return isCurrent; }
    public void setIsCurrent(String isCurrent) { this.isCurrent = isCurrent; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
