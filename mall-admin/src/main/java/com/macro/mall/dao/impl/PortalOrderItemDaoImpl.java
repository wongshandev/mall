package com.macro.mall.dao.impl;


import com.macro.mall.dao.PortalOrderItemDao;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单商品信息自定义Dao
 * Created by macro on 2018/9/3.
 */
@Component
public class PortalOrderItemDaoImpl implements PortalOrderItemDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertOrder(OmsOrder record) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into oms_order ( member_id,order_sn, create_time, total_amount, \n" +
                "      pay_amount, freight_amount, discount_amount, \n" +
                "      pay_type, source_type, status, order_type, \n" +
                "      receiver_name, receiver_phone, receiver_post_code, \n" +
                "      receiver_province, receiver_city, receiver_region, \n" +
                "      receiver_detail_address, note, confirm_status, delete_status) " +
                " values (1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        jdbcTemplate.update(sql.toString()
                , record.getOrderSn()
                , record.getCreateTime()
                , record.getTotalAmount()
                , record.getPayAmount()
                , record.getFreightAmount()
                , record.getDiscountAmount()
                , record.getPayType()
                , record.getSourceType()
                , record.getStatus()
                , record.getOrderType()
                , record.getReceiverName()
                , record.getReceiverPhone()
                , record.getReceiverPostCode()
                , record.getReceiverProvince()
                , record.getReceiverCity()
                , record.getReceiverRegion()
                , record.getReceiverDetailAddress()
                , record.getNote()
                , record.getConfirmStatus()
                , record.getDeleteStatus()
        );
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    @Override
    public int insertOrderItem(OmsOrderItem omsOrderItem) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into oms_order_item (order_id, order_sn, product_id,\n" +
                "        product_pic, product_name, product_brand,\n" +
                "        product_sn, product_price, product_quantity,\n" +
                "        product_sku_id, product_category_id, product_sku_code,\n" +
                "        sp1, sp2, sp3, product_attr ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        return jdbcTemplate.update(sql.toString()
                , omsOrderItem.getOrderId()
                , omsOrderItem.getOrderSn()
                , omsOrderItem.getProductId()
                , omsOrderItem.getProductPic()
                , omsOrderItem.getProductName()
                , omsOrderItem.getProductBrand()
                , omsOrderItem.getProductSn()
                , omsOrderItem.getProductPrice()
                , omsOrderItem.getProductQuantity()
                , omsOrderItem.getProductSkuId()
                , omsOrderItem.getProductCategoryId()
                , omsOrderItem.getProductSkuCode()
                , omsOrderItem.getSp1()
                , omsOrderItem.getSp2()
                , omsOrderItem.getSp3()
                , omsOrderItem.getProductAttr()
        );
    }
}
