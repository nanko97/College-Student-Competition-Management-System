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
 * 竞赛晋级记录实体
 * 数据库表：jingsai_jinji_jilu
 */
@TableName("jingsai_jinji_jilu")
public class JingsaiJinjiJiluEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long yuanBaomingId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long xinBaomingId;
    private String xuehao;
    private String xueshengxingming;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long yuanJingsaiId;
    private String yuanJingsaimingcheng;
    private String yuanJibie;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long xinJingsaiId;
    private String xinJingsaimingcheng;
    private String xinJibie;
    private String jinjiJibie;
    private BigDecimal yuanChengji;
    private Integer yuanMingci;
    private String jinjiYuanyin;
    private String shenheRen;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenheShijian;

    private String jinjiZhuangtai;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getYuanBaomingId() { return yuanBaomingId; }
    public void setYuanBaomingId(Long yuanBaomingId) { this.yuanBaomingId = yuanBaomingId; }
    public Long getXinBaomingId() { return xinBaomingId; }
    public void setXinBaomingId(Long xinBaomingId) { this.xinBaomingId = xinBaomingId; }
    public String getXuehao() { return xuehao; }
    public void setXuehao(String xuehao) { this.xuehao = xuehao; }
    public String getXueshengxingming() { return xueshengxingming; }
    public void setXueshengxingming(String xueshengxingming) { this.xueshengxingming = xueshengxingming; }
    public Long getYuanJingsaiId() { return yuanJingsaiId; }
    public void setYuanJingsaiId(Long yuanJingsaiId) { this.yuanJingsaiId = yuanJingsaiId; }
    public String getYuanJingsaimingcheng() { return yuanJingsaimingcheng; }
    public void setYuanJingsaimingcheng(String yuanJingsaimingcheng) { this.yuanJingsaimingcheng = yuanJingsaimingcheng; }
    public String getYuanJibie() { return yuanJibie; }
    public void setYuanJibie(String yuanJibie) { this.yuanJibie = yuanJibie; }
    public Long getXinJingsaiId() { return xinJingsaiId; }
    public void setXinJingsaiId(Long xinJingsaiId) { this.xinJingsaiId = xinJingsaiId; }
    public String getXinJingsaimingcheng() { return xinJingsaimingcheng; }
    public void setXinJingsaimingcheng(String xinJingsaimingcheng) { this.xinJingsaimingcheng = xinJingsaimingcheng; }
    public String getXinJibie() { return xinJibie; }
    public void setXinJibie(String xinJibie) { this.xinJibie = xinJibie; }
    public String getJinjiJibie() { return jinjiJibie; }
    public void setJinjiJibie(String jinjiJibie) { this.jinjiJibie = jinjiJibie; }
    public BigDecimal getYuanChengji() { return yuanChengji; }
    public void setYuanChengji(BigDecimal yuanChengji) { this.yuanChengji = yuanChengji; }
    public Integer getYuanMingci() { return yuanMingci; }
    public void setYuanMingci(Integer yuanMingci) { this.yuanMingci = yuanMingci; }
    public String getJinjiYuanyin() { return jinjiYuanyin; }
    public void setJinjiYuanyin(String jinjiYuanyin) { this.jinjiYuanyin = jinjiYuanyin; }
    public String getShenheRen() { return shenheRen; }
    public void setShenheRen(String shenheRen) { this.shenheRen = shenheRen; }
    public Date getShenheShijian() { return shenheShijian; }
    public void setShenheShijian(Date shenheShijian) { this.shenheShijian = shenheShijian; }
    public String getJinjiZhuangtai() { return jinjiZhuangtai; }
    public void setJinjiZhuangtai(String jinjiZhuangtai) { this.jinjiZhuangtai = jinjiZhuangtai; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
