package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛晋级关系实体
 * 数据库表：jingsai_jinji_guanxi
 */
@TableName("jingsai_jinji_guanxi")
public class JingsaiJinjiGuanxiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fuJingsaiId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ziJingsaiId;
    private String fuJibie;
    private String ziJibie;
    private String jinjiType;
    private String jinjiRule;
    private Integer zuidiFenshu;
    private Integer zuidiMingci;
    private String isActive;

    // 非数据库字段，用于显示（不映射到数据库表）
    @TableField(exist = false)
    private String fuJingsaimingcheng;
    @TableField(exist = false)
    private String ziJingsaimingcheng;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFuJingsaiId() { return fuJingsaiId; }
    public void setFuJingsaiId(Long fuJingsaiId) { this.fuJingsaiId = fuJingsaiId; }
    public Long getZiJingsaiId() { return ziJingsaiId; }
    public void setZiJingsaiId(Long ziJingsaiId) { this.ziJingsaiId = ziJingsaiId; }
    public String getFuJibie() { return fuJibie; }
    public void setFuJibie(String fuJibie) { this.fuJibie = fuJibie; }
    public String getZiJibie() { return ziJibie; }
    public void setZiJibie(String ziJibie) { this.ziJibie = ziJibie; }
    public String getJinjiType() { return jinjiType; }
    public void setJinjiType(String jinjiType) { this.jinjiType = jinjiType; }
    public String getJinjiRule() { return jinjiRule; }
    public void setJinjiRule(String jinjiRule) { this.jinjiRule = jinjiRule; }
    public Integer getZuidiFenshu() { return zuidiFenshu; }
    public void setZuidiFenshu(Integer zuidiFenshu) { this.zuidiFenshu = zuidiFenshu; }
    public Integer getZuidiMingci() { return zuidiMingci; }
    public void setZuidiMingci(Integer zuidiMingci) { this.zuidiMingci = zuidiMingci; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public String getFuJingsaimingcheng() { return fuJingsaimingcheng; }
    public void setFuJingsaimingcheng(String fuJingsaimingcheng) { this.fuJingsaimingcheng = fuJingsaimingcheng; }
    public String getZiJingsaimingcheng() { return ziJingsaimingcheng; }
    public void setZiJingsaimingcheng(String ziJingsaimingcheng) { this.ziJingsaimingcheng = ziJingsaimingcheng; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
