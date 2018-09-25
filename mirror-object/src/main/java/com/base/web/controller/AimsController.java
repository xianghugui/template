package com.base.web.controller;


import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping(value = "/aims")
@AccessLogger("服务器管理")
@Authorize(module = "aims")
public class AimsController {

    @RequestMapping(value = "/uploadFaceImage", method = RequestMethod.POST)
    @AccessLogger("上传图片")
    @Authorize(action = "C")
    public ResponseMessage add(@RequestParam("file") MultipartFile[] files) {
        System.out.println(files);
        return ResponseMessage.ok();
    }
}
