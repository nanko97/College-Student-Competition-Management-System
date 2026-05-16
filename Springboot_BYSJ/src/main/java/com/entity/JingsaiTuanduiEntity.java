package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 竞赛团队实体
 * 数据库表：jingsai_tuandui
 */
@TableName("jingsai_tuandui")
public class JingsaiTuanduiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    private String tuanduiBianhao;
    private Long jingsaiId;
    private String jingsaimingcheng;
    private Long saidaoId;
    private String saidaoMingcheng;
    private String tuanduiMingcheng;
    private String fuzerenXuehao;
    private String fuzerenXingming;
    private String fuzerenShouji;
    private Integer chengyuanRenshu;
    private String zuopinMingcheng;
    private String zuopinJieshao;
    private String shenheZhuangtai;
    private String shenheYijian;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTuanduiBianhao() { return tuanduiBianhao; }
    public void setTuanduiBianhao(String tuanduiBianhao) { this.tuanduiBianhao = tuanduiBianhao; }
    public Long getJingsaiId() { return jingsaiId; }
    public void setJingsaiId(Long jingsaiId) { this.jingsaiId = jingsaiId; }
    public String getJingsaimingcheng() { return jingsaimingcheng; }
    public void setJingsaimingcheng(String jingsaimingcheng) { this.jingsaimingcheng = jingsaimingcheng; }
    public Long getSaidaoId() { return saidaoId; }
    public void setSaidaoId(Long saidaoId) { this.saidaoId = saidaoId; }
    public String getSaidaoMingcheng() { return saidaoMingcheng; }
    public void setSaidaoMingcheng(String saidaoMingcheng) { this.saidaoMingcheng = saidaoMingcheng; }
    public String getTuanduiMingcheng() { return tuanduiMingcheng; }
    public void setTuanduiMingcheng(String tuanduiMingcheng) { this.tuanduiMingcheng = tuanduiMingcheng; }
    public String getFuzerenXuehao() { return fuzerenXuehao; }
    public void setFuzerenXuehao(String fuzerenXuehao) { this.fuzerenXuehao = fuzerenXuehao; }
    public String getFuzerenXingming() { return fuzerenXingming; }
    public void setFuzerenXingming(String fuzerenXingming) { this.fuzerenXingming = fuzerenXingming; }
    public String getFuzerenShouji() { return fuzerenShouji; }
    public void setFuzerenShouji(String fuzerenShouji) { this.fuzerenShouji = fuzerenShouji; }
    public Integer getChengyuanRenshu() { return chengyuanRenshu; }
    public void setChengyuanRenshu(Integer chengyuanRenshu) { this.chengyuanRenshu = chengyuanRenshu; }
    public String getZuopinMingcheng() { return zuopinMingcheng; }
    public void setZuopinMingcheng(String zuopinMingcheng) { this.zuopinMingcheng = zuopinMingcheng; }
    public String getZuopinJieshao() { return zuopinJieshao; }
    public void setZuopinJieshao(String zuopinJieshao) { this.zuopinJieshao = zuopinJieshao; }
    public String getShenheZhuangtai() { return shenheZhuangtai; }
    public void setShenheZhuangtai(String shenheZhuangtai) { this.shenheZhuangtai = shenheZhuangtai; }
    public String getShenheYijian() { return shenheYijian; }
    public void setShenheYijian(String shenheYijian) { this.shenheYijian = shenheYijian; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
