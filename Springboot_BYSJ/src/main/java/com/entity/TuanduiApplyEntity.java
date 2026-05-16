package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 团队加入/退出申请表
 */
@TableName("tuandui_apply")
public class TuanduiApplyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private Long tuanduiId;
    private String tuanduiMingcheng;
    private Long jingsaiId; // 新增：竞赛ID
    private String jingsaimingcheng;
    private String xuehao;
    private String xueshengxingming;
    private String applyType; // 加入/退出
    private String applyReason;
    private String applyStatus; // 待审核/已通过/已驳回
    private String shenheYijian;
    private String shenheXuehao; // 审核人（负责人）
    private String shenheXingming;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date applyTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenheTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTuanduiId() { return tuanduiId; }
    public void setTuanduiId(Long tuanduiId) { this.tuanduiId = tuanduiId; }
    public String getTuanduiMingcheng() { return tuanduiMingcheng; }
    public void setTuanduiMingcheng(String tuanduiMingcheng) { this.tuanduiMingcheng = tuanduiMingcheng; }
    public Long getJingsaiId() { return jingsaiId; }
    public void setJingsaiId(Long jingsaiId) { this.jingsaiId = jingsaiId; }
    public String getJingsaimingcheng() { return jingsaimingcheng; }
    public void setJingsaimingcheng(String jingsaimingcheng) { this.jingsaimingcheng = jingsaimingcheng; }
    public String getXuehao() { return xuehao; }
    public void setXuehao(String xuehao) { this.xuehao = xuehao; }
    public String getXueshengxingming() { return xueshengxingming; }
    public void setXueshengxingming(String xueshengxingming) { this.xueshengxingming = xueshengxingming; }
    public String getApplyType() { return applyType; }
    public void setApplyType(String applyType) { this.applyType = applyType; }
    public String getApplyReason() { return applyReason; }
    public void setApplyReason(String applyReason) { this.applyReason = applyReason; }
    public String getApplyStatus() { return applyStatus; }
    public void setApplyStatus(String applyStatus) { this.applyStatus = applyStatus; }
    public String getShenheYijian() { return shenheYijian; }
    public void setShenheYijian(String shenheYijian) { this.shenheYijian = shenheYijian; }
    public String getShenheXuehao() { return shenheXuehao; }
    public void setShenheXuehao(String shenheXuehao) { this.shenheXuehao = shenheXuehao; }
    public String getShenheXingming() { return shenheXingming; }
    public void setShenheXingming(String shenheXingming) { this.shenheXingming = shenheXingming; }
    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getShenheTime() { return shenheTime; }
    public void setShenheTime(Date shenheTime) { this.shenheTime = shenheTime; }
}
