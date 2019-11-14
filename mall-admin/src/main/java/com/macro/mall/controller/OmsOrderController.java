package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.service.OmsOrderService;
import com.macro.mall.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 订单管理Controller
 * Created by macro on 2018/10/11.
 */
@Controller
@Api(tags = "OmsOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsOrderController {
    @Autowired
    private OmsOrderService orderService;

    @ApiOperation("查询订单")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrder>> list(OmsOrderQueryParam queryParam,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsOrder> orderList = orderService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(orderList));
    }

    @ApiOperation("批量发货")
    @RequestMapping(value = "/update/delivery", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量关闭订单")
    @RequestMapping(value = "/update/close", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
        int count = orderService.close(ids, note);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("批量删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = orderService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取订单详情:订单信息、商品信息、操作记录")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long id) {
        OmsOrderDetail orderDetailResult = orderService.detail(id);
        return CommonResult.success(orderDetailResult);
    }

    @ApiOperation("修改收货人信息")
    @RequestMapping(value = "/update/receiverInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改订单费用信息")
    @RequestMapping(value = "/update/moneyInfo", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
        int count = orderService.updateMoneyInfo(moneyInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("备注订单")
    @RequestMapping(value = "/update/note", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateNote(@RequestParam("id") Long id,
                                   @RequestParam("note") String note,
                                   @RequestParam("status") Integer status) {
        int count = orderService.updateNote(id, note, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("下单")
    @PostMapping("generateOrder")
    public ModelAndView generateOrder(OmsCartItem cartItem) {
        String uri = "redirect:/product/details/" + cartItem.getProductId();
        CommonResult commonResult = orderService.generateOrder(cartItem);
        ModelAndView mv = new ModelAndView();
        mv.setViewName(uri);
        mv.addObject("msg", commonResult.getMessage());
        return mv;
    }

    @PostMapping("/export")
    public void exportOrders(HttpServletResponse response,OmsOrderQueryParam queryParam) {

        //查询导入失败数据
        List<OmsOrderDetail> orderList = orderService.listItems(queryParam);
        // 设置excel第一行的标题
        String[] titleRow = new String[]{
                "订单编号"
                , "下单时间"
                , "支付总额"
                , "支付方式"
                , "订单状态"
                , "收货人姓名"
                , "收货人电话"
                , "收货人邮编"
                , "收货人地址"
                , "购买商品ID"
                , "购买商品名称"
                , "购买数量"
        };
        ArrayList<HashMap<String, Object>> excelList = new ArrayList<>();
        //获取导入到excel中数据
       /* for (HashMap<String, Object> map : list) {
            HashMap<String, Object> data = (HashMap<String, Object>) map.get("data");
            excelList.add(data);
        }*/
        HashMap<String, Object> data = new HashMap<>();
        data.put("1","1");
        data.put("2","2");
        data.put("3","3");
        excelList.add(data);
        try {
            //生成excel
            FileUtil.writeExcelByMaps(response, excelList);
        } catch (Exception e) {
            e.printStackTrace();
        }






    }

    private String[] excelData(List<OmsOrderItem> orderItemList) {
        String[] strings = new String[3];
        for (OmsOrderItem i : orderItemList) {
            strings[0] += i.getProductId() + ",";
            strings[1] += i.getProductName() + ",";
            strings[2] += i.getProductQuantity() + ",";
        }
        return strings;
    }
}
