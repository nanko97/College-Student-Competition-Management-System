package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛赛道实体
 * 数据库表：jingsai_saidao
 */
@TableName("jingsai_saidao")
public class JingsaiSaidaoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private Long jingsaiId;
    private String jingsaimingcheng;
    private String saidaoMingcheng;
    private String saidaoBianma;
    private String cansaiLeixing;
    private Integer tuanduiRenshuMin;
    private Integer tuanduiRenshuMax;
    private String baomingShuoming;
    private String pingshenBiaozhun;
    private String isActive;
    private Integer sortOrder;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJingsaiId() { return jingsaiId; }
    public void setJingsaiId(Long jingsaiId) { this.jingsaiId = jingsaiId; }
    public String getJingsaimingcheng() { return jingsaimingcheng; }
    public void setJingsaimingcheng(String jingsaimingcheng) { this.jingsaimingcheng = jingsaimingcheng; }
    public String getSaidaoMingcheng() { return saidaoMingcheng; }
    public void setSaidaoMingcheng(String saidaoMingcheng) { this.saidaoMingcheng = saidaoMingcheng; }
    public String getSaidaoBianma() { return saidaoBianma; }
    public void setSaidaoBianma(String saidaoBianma) { this.saidaoBianma = saidaoBianma; }
    public String getCansaiLeixing() { return cansaiLeixing; }
    public void setCansaiLeixing(String cansaiLeixing) { this.cansaiLeixing = cansaiLeixing; }
    public Integer getTuanduiRenshuMin() { return tuanduiRenshuMin; }
    public void setTuanduiRenshuMin(Integer tuanduiRenshuMin) { this.tuanduiRenshuMin = tuanduiRenshuMin; }
    public Integer getTuanduiRenshuMax() { return tuanduiRenshuMax; }
    public void setTuanduiRenshuMax(Integer tuanduiRenshuMax) { this.tuanduiRenshuMax = tuanduiRenshuMax; }
    public String getBaomingShuoming() { return baomingShuoming; }
    public void setBaomingShuoming(String baomingShuoming) { this.baomingShuoming = baomingShuoming; }
    public String getPingshenBiaozhun() { return pingshenBiaozhun; }
    public void setPingshenBiaozhun(String pingshenBiaozhun) { this.pingshenBiaozhun = pingshenBiaozhun; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
