package com.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.entity.ZuopindafenEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 作品打分
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2021-05-03 11:24:10
 */
@TableName("zuopindafen")
public class ZuopindafenView extends ZuopindafenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ZuopindafenView() {
    }

    public ZuopindafenView(ZuopindafenEntity zuopindafenEntity) {
        try {
            BeanUtils.copyProperties(this, zuopindafenEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
