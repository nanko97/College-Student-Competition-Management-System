package com.entity.view;

import com.entity.XiaoxiTongzhiEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * 消息通知视图类（前端展示用）
 */
@TableName("xiaoxi_tongzhi")
public class XiaoxiTongzhiView extends XiaoxiTongzhiEntity {
    private static final long serialVersionUID = 1L;

    public XiaoxiTongzhiView() {}

    public XiaoxiTongzhiView(XiaoxiTongzhiEntity xiaoxiTongzhiEntity) {
        try {
            BeanUtils.copyProperties(this, xiaoxiTongzhiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
