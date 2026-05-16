package com.entity.vo;

import com.entity.ZuopindafenFuheEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * 作品打分复核申请VO
 */
public class ZuopindafenFuheVO implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZuopindafenFuheVO() {
	}

	public ZuopindafenFuheVO(ZuopindafenFuheEntity zuopindafenFuheEntity) {
		try {
			BeanUtils.copyProperties(this, zuopindafenFuheEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private Long id;
	private Long zuopindafenId;
	private String jingsaimingcheng;
	private String yuanChengji;
	private String xuehao;
	private String xueshengxingming;
	private String fuheReason;
	private String fuheStatus;
	private String xinChengji;
	private String shenheYijian;
	private String shenheGonghao;
	private String shenheJiaoshi;
	private Date shenqingShijian;
	private Date shenheShijian;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getZuopindafenId() {
		return zuopindafenId;
	}

	public void setZuopindafenId(Long zuopindafenId) {
		this.zuopindafenId = zuopindafenId;
	}

	public String getJingsaimingcheng() {
		return jingsaimingcheng;
	}

	public void setJingsaimingcheng(String jingsaimingcheng) {
		this.jingsaimingcheng = jingsaimingcheng;
	}

	public String getYuanChengji() {
		return yuanChengji;
	}

	public void setYuanChengji(String yuanChengji) {
		this.yuanChengji = yuanChengji;
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

	public String getFuheReason() {
		return fuheReason;
	}

	public void setFuheReason(String fuheReason) {
		this.fuheReason = fuheReason;
	}

	public String getFuheStatus() {
		return fuheStatus;
	}

	public void setFuheStatus(String fuheStatus) {
		this.fuheStatus = fuheStatus;
	}

	public String getXinChengji() {
		return xinChengji;
	}

	public void setXinChengji(String xinChengji) {
		this.xinChengji = xinChengji;
	}

	public String getShenheYijian() {
		return shenheYijian;
	}

	public void setShenheYijian(String shenheYijian) {
		this.shenheYijian = shenheYijian;
	}

	public String getShenheGonghao() {
		return shenheGonghao;
	}

	public void setShenheGonghao(String shenheGonghao) {
		this.shenheGonghao = shenheGonghao;
	}

	public String getShenheJiaoshi() {
		return shenheJiaoshi;
	}

	public void setShenheJiaoshi(String shenheJiaoshi) {
		this.shenheJiaoshi = shenheJiaoshi;
	}

	public Date getShenqingShijian() {
		return shenqingShijian;
	}

	public void setShenqingShijian(Date shenqingShijian) {
		this.shenqingShijian = shenqingShijian;
	}

	public Date getShenheShijian() {
		return shenheShijian;
	}

	public void setShenheShijian(Date shenheShijian) {
		this.shenheShijian = shenheShijian;
	}
}
