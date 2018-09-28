package com.base.web.controller;

import com.base.web.bean.Camera;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import com.base.web.service.FaceImageService;
import com.base.web.service.ServerDeviceService;
import com.base.web.util.NetDvrInit;
import com.base.web.util.ResourceUtil;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.base.web.core.message.ResponseMessage.ok;

@RestController
@RequestMapping(value = "/camera")
@AccessLogger("摄像头管理")
@Authorize(module = "camera")
public class CameraController extends GenericController<Camera, Long> {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private ServerDeviceService serverDeviceService;

    @PostMapping
    @AccessLogger("新增")
    @Authorize(action = "C")
    @Override
    public ResponseMessage add(@RequestBody Camera camera) {
        String message = camera.validate();
        if (message != null) {
            return ResponseMessage.error(message);
        }
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
        //如果有关联服务器重新布防，没有就直接更新
        ServerDevice serverDevice = serverDeviceService.createQuery().where(ServerDevice.Property.deviceId, id).single();
        if (serverDevice != null) {
            //报警撤防，登出
            NetDvrInit.closeAlarmChan(oldCamera.getAlarmHandleId());
            NetDvrInit.logout(oldCamera.getUserId());
            Long userId = NetDvrInit.login(camera);
            //登陆
            if (userId == 0xFFFFFFFF || userId == 0xFFFFFFFFL) {
                logger.error("修改摄像头时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                        + "password:"+ camera.getPassword() + "登陆失败，错误码：" + NetDvrInit.getLastError());
            } else {
                camera.setUserId(userId);
            }
            //报警布防
            Long alarmHandleId = NetDvrInit.setupAlarmChan(userId);
            if (alarmHandleId == 0xFFFFFFFF || userId == 0xFFFFFFFFL) {
                logger.error("修改摄像头时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                        + "password:"+ camera.getPassword() + "报警布防失败，错误码：" + NetDvrInit.getLastError());
                return ResponseMessage.error("报警布防失败(请检查IP/端口/用户名/密码是否错误)，错误码:" + NetDvrInit.getLastError());
            } else {
                camera.setAlarmHandleId(alarmHandleId);
            }
        }
        camera.setId(id);
        getService().update(camera);
        return ResponseMessage.ok("修改成功");
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

    @Override
    @DeleteMapping(value = "/{id}")
    @AccessLogger("删除")
    @Authorize(action = "D")
    public ResponseMessage delete(@PathVariable("id") Long id) {
        Camera camera = getService().selectByPk(id);
        assertFound(camera, "data is not found!");
        getService().delete(id);
        NetDvrInit.closeAlarmChan(camera.getAlarmHandleId());
        NetDvrInit.logout(camera.getUserId());
        return ok("删除成功");
    }
}
