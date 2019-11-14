package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dao.OmsOrderDao;
import com.macro.mall.dao.OmsOrderOperateHistoryDao;
import com.macro.mall.dao.impl.PortalOrderItemDaoImpl;
import com.macro.mall.dto.*;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderOperateHistoryMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by macro on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;
    @Autowired
    private PortalOrderItemDaoImpl portalOrderItemDao;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }@Override
    public List<OmsOrderDetail> listItems(OmsOrderQueryParam queryParam) {
        return orderDao.getItemList(queryParam);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货
        int count = orderDao.delivery(deliveryParamList);
        //添加操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        int count = orderMapper.updateByExampleSelective(record, example);
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:" + note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注信息：" + note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    @Transactional
    public CommonResult generateOrder(OmsCartItem omsCartItem) {

        //生成下单商品信息
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setProductId(omsCartItem.getProductId());
        orderItem.setProductName(omsCartItem.getProductName());
        orderItem.setProductPic(omsCartItem.getProductPic());
        orderItem.setProductAttr(omsCartItem.getProductAttr());
        orderItem.setProductBrand(omsCartItem.getProductBrand());
        orderItem.setProductSn(omsCartItem.getProductSn());
        orderItem.setProductPrice(omsCartItem.getPrice());
        orderItem.setProductQuantity(omsCartItem.getQuantity());
        orderItem.setProductSkuId(omsCartItem.getProductSkuId());
        orderItem.setProductSkuCode(omsCartItem.getProductSkuCode());
        orderItem.setProductCategoryId(omsCartItem.getProductCategoryId());

        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        order.setFreightAmount(new BigDecimal(0));
        order.setPayAmount(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
        //转化为订单信息并插入数据库

        order.setCreateTime(new Date());

        //支付方式：0->未支付；1->支付宝；2->微信；3->货到付款
        order.setPayType(3);
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        order.setReceiverName(omsCartItem.getName());
        order.setReceiverPhone(omsCartItem.getPhoneNumber());
        order.setReceiverPostCode(omsCartItem.getPostCode());
        order.setReceiverProvince(omsCartItem.getProvince());
        order.setReceiverCity(omsCartItem.getCity());
        order.setReceiverRegion(omsCartItem.getRegion());
        order.setReceiverDetailAddress(omsCartItem.getDetailAddress());
        order.setNote(omsCartItem.getNote());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //生成订单号
        order.setOrderSn(generateOrderSn(order));

        //插入order表和order_item表
        Long insert = Long.valueOf(portalOrderItemDao.insertOrder(order));
        orderItem.setOrderId(insert);
        orderItem.setOrderSn(order.getOrderSn());
        portalOrderItemDao.insertOrderItem(orderItem);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItem);
        return CommonResult.success("下单成功");
    }


    /**
     * 生成订单编号
     */
    private static Integer number = 0;
    private static int maxNum = 200000;
    private static String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
        if (!date.equals(now)) date = now;
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        number++;//唯一数字自增
        if (number >= maxNum) { // 值的上限，超过就归零
            number = maxNum - 200000;
        }
        sb.append(String.format("%06d", number));
        return sb.toString();
    }

}
