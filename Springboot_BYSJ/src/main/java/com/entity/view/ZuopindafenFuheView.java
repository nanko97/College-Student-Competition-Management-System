package com.entity.view;

import com.entity.ZuopindafenFuheEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * 作品打分复核申请View
 */
@TableName("zuopindafen_fuhe")
public class ZuopindafenFuheView extends ZuopindafenFuheEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZuopindafenFuheView(){
	}

	public ZuopindafenFuheView(ZuopindafenFuheEntity zuopindafenFuheEntity){
		try {
			BeanUtils.copyProperties(this, zuopindafenFuheEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("鎿嶄綔澶辫触", e);
		}
	}
}
