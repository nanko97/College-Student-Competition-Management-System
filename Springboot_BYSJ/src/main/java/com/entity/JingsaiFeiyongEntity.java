package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 竞赛费用配置实体
 * 数据库表：jingsai_feiyong
 */
@TableName("jingsai_feiyong")
public class JingsaiFeiyongEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long jingsaiId;
    private String jingsaimingcheng;
    private BigDecimal baomingfei;
    private String shifouShoufei;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date jiaofeiJiezhiRiqi;

    private String beizhu;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJingsaiId() {
        return jingsaiId;
    }

    public void setJingsaiId(Long jingsaiId) {
        this.jingsaiId = jingsaiId;
    }

    public String getJingsaimingcheng() {
        return jingsaimingcheng;
    }

    public void setJingsaimingcheng(String jingsaimingcheng) {
        this.jingsaimingcheng = jingsaimingcheng;
    }

    public BigDecimal getBaomingfei() {
        return baomingfei;
    }

    public void setBaomingfei(BigDecimal baomingfei) {
        this.baomingfei = baomingfei;
    }

    public String getShifouShoufei() {
        return shifouShoufei;
    }

    public void setShifouShoufei(String shifouShoufei) {
        this.shifouShoufei = shifouShoufei;
    }

    public Date getJiaofeiJiezhiRiqi() {
        return jiaofeiJiezhiRiqi;
    }

    public void setJiaofeiJiezhiRiqi(Date jiaofeiJiezhiRiqi) {
        this.jiaofeiJiezhiRiqi = jiaofeiJiezhiRiqi;
    }

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }
}
