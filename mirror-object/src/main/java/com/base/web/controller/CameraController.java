package com.base.web.controller;

import com.base.web.bean.Camera;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/camera")
@AccessLogger("摄像头管理")
@Authorize(module = "camera")
public class CameraController extends GenericController<Camera, Long> {

    @Autowired
    private CameraService cameraService;

    @PostMapping
    @AccessLogger("新增")
    @Authorize(action = "C")
    @Override
    public ResponseMessage add(@RequestBody Camera camera) {
        camera.setStatus(0);
        camera.setCreateTime(new Date());
        return ResponseMessage.ok(getService().insert(camera));
    }

    @Override
    protected CameraService getService() {
        return cameraService;
    }
}
