package com.base.web.controller;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import com.base.web.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/server")
@AccessLogger("服务器管理")
@Authorize(module = "server")
public class ServerController {

    @Autowired
    private ServerService serverService;

    @Autowired
    private CameraService cameraService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @AccessLogger("添加服务器")
    @Authorize(action = "C")
    public ResponseMessage add(@RequestBody Server data) {
        data.setCreateTime(new Date());
        return ResponseMessage.ok(serverService.insert(data));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @AccessLogger("删除服务器")
    @Authorize(action = "D")
    public ResponseMessage delete(@PathVariable("id") Long id) {
        return ResponseMessage.ok(serverService.delete(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @AccessLogger("修改节点")
    @Authorize(action = "U")
    public ResponseMessage update(@RequestBody Server data) {
        return ResponseMessage.ok(serverService.update(data));
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @AccessLogger("查询服务器详情")
    @Authorize(action = "R")
    public ResponseMessage serverInfo(QueryParam param) {
        return ResponseMessage.ok(serverService.select(param));
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @AccessLogger("查询服务器列表")
    @Authorize(action = "R")
    public ResponseMessage listServer(QueryParam param) {
        return ResponseMessage.ok(serverService.select(param));
    }

    @RequestMapping(value = "/queryDevice", method = RequestMethod.GET)
    @AccessLogger("查询没设防的设备")
    @Authorize(action = "R")
    public ResponseMessage queryDevice() {
        List<Camera> queryList = cameraService.createQuery()
                .where(Camera.Property.STATUS,0).list();
        return ResponseMessage.ok(queryList);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @AccessLogger("服务器关联设备")
    @Authorize(action = "C")
    public ResponseMessage addDevice(@RequestBody ServerDevice data) {
        return ResponseMessage.ok(serverService.addDevice(data));
    }



}
