package com.base.web.controller;

import com.base.web.bean.Camera;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import com.base.web.service.FaceImageService;
import com.base.web.util.NetDvrInit;
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
    @PutMapping(value = "/{id}")
    @AccessLogger("修改")
    @Authorize(action = "U")
    public ResponseMessage update(@PathVariable Long id, @RequestBody Camera camera) {
        String message = camera.validate();
        if (message != null) {
            return ResponseMessage.error(message);
        }
        Camera oldCamera = getService().selectByPk(id);
        assertFound(oldCamera, "data is not found!");
        //报警撤防，登出
        NetDvrInit.closeAlarmChan(oldCamera.getAlarmHandleId());
        NetDvrInit.logout(oldCamera.getUserId());
        Long userId = NetDvrInit.login(camera);
        //登陆
        if (userId == -1L) {
            logger.error("修改摄像头时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                    + "password:"+ camera.getPassword() + "登陆失败，错误码：" + NetDvrInit.getLastError());
        }
        userId = NetDvrInit.login(camera);
        //登陆
        if (userId == -1L) {
            logger.error("修改摄像头时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                    + "password:"+ camera.getPassword() + "登陆失败，错误码：" + NetDvrInit.getLastError());
        }
        //报警布防
        Long alarmHandleId = NetDvrInit.setupAlarmChan(userId);
        if (alarmHandleId == -1L) {
            logger.error("修改摄像头时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                    + "password:"+ camera.getPassword() + "报警布防失败，错误码：" + NetDvrInit.getLastError());
        }
        camera.setId(id);
        getService().update(camera);
        return ResponseMessage.ok(alarmHandleId == -1 ? "报警布防失败(请检查IP/端口/用户名/密码是否错误)，错误码:" + NetDvrInit.getLastError() : "修改成功");
    }

    @Override
    protected CameraService getService() {
        return cameraService;
    }

    @GetMapping(value = "/select")
    @AccessLogger("查询")
    @Authorize(action = "R")
    public ResponseMessage list(QueryParam param, HttpServletRequest req) {
        PagerResult<Map> faceImageList = faceImageService.queryAllFaceImage(param,req);
        return ResponseMessage.ok(faceImageList)
                .include(getPOType(), param.getIncludes())
                .exclude(getPOType(), param.getExcludes())
                .onlyData();
    }
}
