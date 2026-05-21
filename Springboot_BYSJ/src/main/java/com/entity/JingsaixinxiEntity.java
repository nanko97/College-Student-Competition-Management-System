package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 竞赛信息
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
@TableName("jingsaixinxi")
public class JingsaixinxiEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;


    public JingsaixinxiEntity() {

    }

    public JingsaixinxiEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 主键id
     */
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 竞赛名称
     */
    @TableField("jingsaimingcheng")
    private String jingsaimingcheng;

    /**
     * 竞赛类型
     */
    @TableField("jingsaileixing")
    private String jingsaileixing;

    /**
     * 竞赛级别(A类/B类/C类)
     */
    @TableField("jingsai_jibie")
    private String jingsaiJibie;

    /**
     * 年份
     */
    @TableField("nianfen")
    private Integer nianfen;

    /**
     * 竞赛地点
     */
    @TableField("jingsaididian")
    private String jingsaididian;

    /**
     * 竞赛规则
     */
    @TableField("jingsaiguize")
    private String jingsaiguize;

    /**
     * 竞赛奖励
     */
    @TableField("jingsaijiangli")
    private String jingsaijiangli;

    /**
     * 竞赛时间
     */
    @TableField("jingsaishijian")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date jingsaishijian;

    /**
     * 模式
     */
    @TableField("moshi")
    private String moshi;

    /**
     * 是否有赛道(是/否)
     */
    @TableField("shifou_you_saidao")
    private String shifouYouSaidao;

    /**
     * 是否需要晋级(是/否)
     */
    @TableField("shifou_xuyao_jinji")
    private String shifouXuyaoJinji;

    /**
     * 封面图片
     */
    @TableField("fengmiantupian")
    private String fengmiantupian;

    /**
     * 工号
     */
    @TableField("gonghao")
    private String gonghao;

    /**
     * 教师姓名
     */
    @TableField("jiaoshixingming")
    private String jiaoshixingming;


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 设置：竞赛名称
     */
    public void setJingsaimingcheng(String jingsaimingcheng) {
        this.jingsaimingcheng = jingsaimingcheng;
    }

    /**
     * 获取：竞赛名称
     */
    public String getJingsaimingcheng() {
        return jingsaimingcheng;
    }

    /**
     * 设置：竞赛类型
     */
    public void setJingsaileixing(String jingsaileixing) {
        this.jingsaileixing = jingsaileixing;
    }

    /**
     * 获取：竞赛类型
     */
    public String getJingsaileixing() {
        return jingsaileixing;
    }

    /**
     * 设置：竞赛地点
     */
    public void setJingsaididian(String jingsaididian) {
        this.jingsaididian = jingsaididian;
    }

    /**
     * 获取：竞赛地点
     */
    public String getJingsaididian() {
        return jingsaididian;
    }

    /**
     * 设置：竞赛规则
     */
    public void setJingsaiguize(String jingsaiguize) {
        this.jingsaiguize = jingsaiguize;
    }

    /**
     * 获取：竞赛规则
     */
    public String getJingsaiguize() {
        return jingsaiguize;
    }

    /**
     * 设置：竞赛奖励
     */
    public void setJingsaijiangli(String jingsaijiangli) {
        this.jingsaijiangli = jingsaijiangli;
    }

    /**
     * 获取：竞赛奖励
     */
    public String getJingsaijiangli() {
        return jingsaijiangli;
    }

    /**
     * 设置：竞赛时间
     */
    public void setJingsaishijian(Date jingsaishijian) {
        this.jingsaishijian = jingsaishijian;
    }

    /**
     * 获取：竞赛时间
     */
    public Date getJingsaishijian() {
        return jingsaishijian;
    }

    /**
     * 设置：模式
     */
    public void setMoshi(String moshi) {
        this.moshi = moshi;
    }

    /**
     * 获取：模式
     */
    public String getMoshi() {
        return moshi;
    }

    /**
     * 设置：竞赛级别
     */
    public void setJingsaiJibie(String jingsaiJibie) {
        this.jingsaiJibie = jingsaiJibie;
    }

    /**
     * 获取：竞赛级别
     */
    public String getJingsaiJibie() {
        return jingsaiJibie;
    }

    /**
     * 设置：年份
     */
    public void setNianfen(Integer nianfen) {
        this.nianfen = nianfen;
    }

    /**
     * 获取：年份
     */
    public Integer getNianfen() {
        return nianfen;
    }

    /**
     * 设置：是否有赛道
     */
    public void setShifouYouSaidao(String shifouYouSaidao) {
        this.shifouYouSaidao = shifouYouSaidao;
    }

    /**
     * 获取：是否有赛道
     */
    public String getShifouYouSaidao() {
        return shifouYouSaidao;
    }

    /**
     * 设置：是否需要晋级
     */
    public void setShifouXuyaoJinji(String shifouXuyaoJinji) {
        this.shifouXuyaoJinji = shifouXuyaoJinji;
    }

    /**
     * 获取：是否需要晋级
     */
    public String getShifouXuyaoJinji() {
        return shifouXuyaoJinji;
    }

    /**
     * 设置：封面图片
     */
    public void setFengmiantupian(String fengmiantupian) {
        this.fengmiantupian = fengmiantupian;
    }

    /**
     * 获取：封面图片
     */
    public String getFengmiantupian() {
        return fengmiantupian;
    }

    /**
     * 设置：工号
     */
    public void setGonghao(String gonghao) {
        this.gonghao = gonghao;
    }

    /**
     * 获取：工号
     */
    public String getGonghao() {
        return gonghao;
    }

    /**
     * 设置：教师姓名
     */
    public void setJiaoshixingming(String jiaoshixingming) {
        this.jiaoshixingming = jiaoshixingming;
    }

    /**
     * 获取：教师姓名
     */
    public String getJiaoshixingming() {
        return jiaoshixingming;
    }

}
