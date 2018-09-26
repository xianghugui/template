package com.base.web.controller;

import com.base.web.bean.Camera;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import com.base.web.service.FaceImageService;
import com.base.web.util.ResourceUtil;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/camera")
@AccessLogger("摄像头管理")
@Authorize(module = "camera")
public class CameraController extends GenericController<Camera, Long> {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private FaceImageService faceImageService;

    @PostMapping
    @AccessLogger("新增")
    @Authorize(action = "C")
    @Override
    public ResponseMessage add(@RequestBody Camera camera) {
        String message = camera.validate();
        if (message != null) return ResponseMessage.error(message);
        camera.setStatus(0);
        camera.setCreateTime(new Date());
        return ResponseMessage.ok(getService().insert(camera));
    }

    @Override
    protected CameraService getService() {
        return cameraService;
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    @AccessLogger("查询")
    @Authorize(action = "R")
    public ResponseMessage select(QueryParam param, HttpServletRequest req) {
        PagerResult<Map> faceImageList = faceImageService.queryAllFaceImage(param,req);
        return ResponseMessage.ok(faceImageList)
                .include(getPOType(), param.getIncludes())
                .exclude(getPOType(), param.getExcludes())
                .onlyData();
    }
}
