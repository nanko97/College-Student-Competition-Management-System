package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
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
 * 竞赛缴费记录实体
 * 数据库表：jingsai_jiaofei_jilu
 */
@TableName("jingsai_jiaofei_jilu")
public class JingsaiJiaofeiJiluEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long baomingId;
    private String xuehao;
    private String xueshengxingming;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jingsaiId;
    private String jingsaimingcheng;
    private BigDecimal jiaofeiJine;
    private String jiaofeiZhuangtai;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date jiaofeiShijian;

    private String shenheRen;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenheShijian;

    private String shenheYijian;
    private String zhifufangshi;
    private String jiaoyiHao;
    private String pingzhengTupian;

    /**
     * 应缴金额（从竞赛费用表关联查询，非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal baomingfei;

    /**
     * 缴费截止日期（从竞赛费用表关联查询，非数据库字段）
     */
    @TableField(exist = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date jiaofeiJiezhiRiqi;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBaomingId() {
        return baomingId;
    }

    public void setBaomingId(Long baomingId) {
        this.baomingId = baomingId;
    }

    public String getXuehao() {
        return xuehao;
    }

    public void setXuehao(String xuehao) {
        this.xuehao = xuehao;
    }

    public String getXueshengxingming() {
        return xueshengxingming;
    }

    public void setXueshengxingming(String xueshengxingming) {
        this.xueshengxingming = xueshengxingming;
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

    public BigDecimal getJiaofeiJine() {
        return jiaofeiJine;
    }

    public void setJiaofeiJine(BigDecimal jiaofeiJine) {
        this.jiaofeiJine = jiaofeiJine;
    }

    public String getJiaofeiZhuangtai() {
        return jiaofeiZhuangtai;
    }

    public void setJiaofeiZhuangtai(String jiaofeiZhuangtai) {
        this.jiaofeiZhuangtai = jiaofeiZhuangtai;
    }

    public Date getJiaofeiShijian() {
        return jiaofeiShijian;
    }

    public void setJiaofeiShijian(Date jiaofeiShijian) {
        this.jiaofeiShijian = jiaofeiShijian;
    }

    public String getShenheRen() {
        return shenheRen;
    }

    public void setShenheRen(String shenheRen) {
        this.shenheRen = shenheRen;
    }

    public Date getShenheShijian() {
        return shenheShijian;
    }

    public void setShenheShijian(Date shenheShijian) {
        this.shenheShijian = shenheShijian;
    }

    public String getShenheYijian() {
        return shenheYijian;
    }

    public void setShenheYijian(String shenheYijian) {
        this.shenheYijian = shenheYijian;
    }

    public String getZhifufangshi() {
        return zhifufangshi;
    }

    public void setZhifufangshi(String zhifufangshi) {
        this.zhifufangshi = zhifufangshi;
    }

    public String getJiaoyiHao() {
        return jiaoyiHao;
    }

    public void setJiaoyiHao(String jiaoyiHao) {
        this.jiaoyiHao = jiaoyiHao;
    }

    public String getPingzhengTupian() {
        return pingzhengTupian;
    }

    public void setPingzhengTupian(String pingzhengTupian) {
        this.pingzhengTupian = pingzhengTupian;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public BigDecimal getBaomingfei() {
        return baomingfei;
    }

    public void setBaomingfei(BigDecimal baomingfei) {
        this.baomingfei = baomingfei;
    }

    public Date getJiaofeiJiezhiRiqi() {
        return jiaofeiJiezhiRiqi;
    }

    public void setJiaofeiJiezhiRiqi(Date jiaofeiJiezhiRiqi) {
        this.jiaofeiJiezhiRiqi = jiaofeiJiezhiRiqi;
    }
}
