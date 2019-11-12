package com.macro.mall.controller;


import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.dto.OssPolicyResult;
import com.macro.mall.service.impl.OssServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Oss相关操作接口
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "OssController", description = "Oss管理")
@RequestMapping("/aliyun/oss")
public class OssController {

    @Value("${files.host}")
    private String HOST;
    @Value("${files.path}")
    private String PATH;
    @Value("${files.absolutePath}")
    private String ABSOLUTEPATH;
    @Value("${files.images.prefix}")
    private String PREFIX;
    @Autowired
    private OssServiceImpl ossService;

    @ApiOperation(value = "oss上传签名生成")
    @RequestMapping(value = "/policy", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OssPolicyResult> policy() {
        OssPolicyResult result = new OssPolicyResult();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        result.setDir(PREFIX+ "/" + sdf.format(new Date()));
        result.setHost(HOST);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "oss上传成功回调")
    @RequestMapping(value = "callback", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<OssCallbackResult> callback(HttpServletRequest request) {
        OssCallbackResult ossCallbackResult = ossService.callback(request);
        return CommonResult.success(ossCallbackResult);
    }

    @RequestMapping("/fileUpload")
    @ResponseBody
    public CommonResult fileUpload(MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            // 存储目录
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileName = ABSOLUTEPATH + PREFIX + "/" + sdf.format(new Date()) + "/" + file.getOriginalFilename();
            File dest = new File(fileName);
            // 判断路径是否存在，如果不存在则创建
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
//            file.transferTo(new File(ABSOLUTEPATH + PREFIX + "/" + file.getOriginalFilename()));
            file.transferTo(dest);
            return CommonResult.success("上传成功");
        } else {
            return CommonResult.failed("文件为空");
        }
    }

}



