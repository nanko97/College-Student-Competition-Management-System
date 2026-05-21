package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛团队成员实体
 * 数据库表：jingsai_tuandui_chengyuan
 */
@TableName("jingsai_tuandui_chengyuan")
public class JingsaiTuanduiChengyuanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tuanduiId;
    private String tuanduiBianhao;
    private String xuehao;
    private String xueshengxingming;
    private String juese;
    private String xueyuan;
    private String zhuanye;
    private String banji;
    private String shouji;
    private String youxiang;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date ruzuiShijian;

    private String isActive;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTuanduiId() { return tuanduiId; }
    public void setTuanduiId(Long tuanduiId) { this.tuanduiId = tuanduiId; }
    public String getTuanduiBianhao() { return tuanduiBianhao; }
    public void setTuanduiBianhao(String tuanduiBianhao) { this.tuanduiBianhao = tuanduiBianhao; }
    public String getXuehao() { return xuehao; }
    public void setXuehao(String xuehao) { this.xuehao = xuehao; }
    public String getXueshengxingming() { return xueshengxingming; }
    public void setXueshengxingming(String xueshengxingming) { this.xueshengxingming = xueshengxingming; }
    public String getJuese() { return juese; }
    public void setJuese(String juese) { this.juese = juese; }
    public String getXueyuan() { return xueyuan; }
    public void setXueyuan(String xueyuan) { this.xueyuan = xueyuan; }
    public String getZhuanye() { return zhuanye; }
    public void setZhuanye(String zhuanye) { this.zhuanye = zhuanye; }
    public String getBanji() { return banji; }
    public void setBanji(String banji) { this.banji = banji; }
    public String getShouji() { return shouji; }
    public void setShouji(String shouji) { this.shouji = shouji; }
    public String getYouxiang() { return youxiang; }
    public void setYouxiang(String youxiang) { this.youxiang = youxiang; }
    public Date getRuzuiShijian() { return ruzuiShijian; }
    public void setRuzuiShijian(Date ruzuiShijian) { this.ruzuiShijian = ruzuiShijian; }
    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
