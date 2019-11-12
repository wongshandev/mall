package com.macro.mall.dao;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;

/**
 * Created by IntelliJ IDEA
 * Author: d-arlin@qq.com
 * Date: 2019/11/3
 * Time: 17:43
 */
public interface PortalOrderItemDao {

    int insertOrder(OmsOrder record);

    int insertOrderItem(OmsOrderItem omsOrderItem);
}
