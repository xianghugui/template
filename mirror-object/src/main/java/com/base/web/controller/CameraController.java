package com.base.web.controller;

import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/camera")
@AccessLogger("摄像头管理")
@Authorize(module = "camera")
public class CameraController {

    @Autowired
    private CameraService cameraService;


}
