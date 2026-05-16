package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛人员变更日志实体
 * 数据库表：jingsai_renyuan_biangueng
 */
@TableName("jingsai_renyuan_biangueng")
public class JingsaiRenyuanBianguengEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private Long tuanduiId;
    private String tuanduiBianhao;
    private Long jingsaiId;
    private String bianguengLeixing;
    private String caozuoQian;
    private String caozuoHou;
    private String caozuoRenXuehao;
    private String caozuoRenXingming;
    private String caozuoYuanyin;
    private String shenheRen;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date shenheShijian;

    private String shenheZhuangtai;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTuanduiId() { return tuanduiId; }
    public void setTuanduiId(Long tuanduiId) { this.tuanduiId = tuanduiId; }
    public String getTuanduiBianhao() { return tuanduiBianhao; }
    public void setTuanduiBianhao(String tuanduiBianhao) { this.tuanduiBianhao = tuanduiBianhao; }
    public Long getJingsaiId() { return jingsaiId; }
    public void setJingsaiId(Long jingsaiId) { this.jingsaiId = jingsaiId; }
    public String getBianguengLeixing() { return bianguengLeixing; }
    public void setBianguengLeixing(String bianguengLeixing) { this.bianguengLeixing = bianguengLeixing; }
    public String getCaozuoQian() { return caozuoQian; }
    public void setCaozuoQian(String caozuoQian) { this.caozuoQian = caozuoQian; }
    public String getCaozuoHou() { return caozuoHou; }
    public void setCaozuoHou(String caozuoHou) { this.caozuoHou = caozuoHou; }
    public String getCaozuoRenXuehao() { return caozuoRenXuehao; }
    public void setCaozuoRenXuehao(String caozuoRenXuehao) { this.caozuoRenXuehao = caozuoRenXuehao; }
    public String getCaozuoRenXingming() { return caozuoRenXingming; }
    public void setCaozuoRenXingming(String caozuoRenXingming) { this.caozuoRenXingming = caozuoRenXingming; }
    public String getCaozuoYuanyin() { return caozuoYuanyin; }
    public void setCaozuoYuanyin(String caozuoYuanyin) { this.caozuoYuanyin = caozuoYuanyin; }
    public String getShenheRen() { return shenheRen; }
    public void setShenheRen(String shenheRen) { this.shenheRen = shenheRen; }
    public Date getShenheShijian() { return shenheShijian; }
    public void setShenheShijian(Date shenheShijian) { this.shenheShijian = shenheShijian; }
    public String getShenheZhuangtai() { return shenheZhuangtai; }
    public void setShenheZhuangtai(String shenheZhuangtai) { this.shenheZhuangtai = shenheZhuangtai; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
