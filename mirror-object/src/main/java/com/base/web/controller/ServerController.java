package com.base.web.controller;

import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.CameraService;
import com.base.web.service.ServerDeviceService;
import com.base.web.service.ServerService;
import com.base.web.util.NetDvrInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping(value = "/server")
@AccessLogger("服务器管理")
@Authorize(module = "server")
public class ServerController extends GenericController<Server, Long>{

    @Autowired
    private ServerService serverService;

    @Override
    protected ServerService getService() {
        return serverService;
    }

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ServerDeviceService serverDeviceService;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @AccessLogger("添加服务器")
    @Authorize(action = "C")
    public ResponseMessage add(@RequestBody Server data) {
        String message = data.validate();
        if (message != null) return ResponseMessage.error(message);

        Server server = serverService.createQuery()
                .where(Server.Property.serverIp,data.getServerIp())
                .and(Server.Property.serverPort,data.getServerPort()).single();
        if(server != null){
            return ResponseMessage.error("该ip地址的端口已被占用");
        }
        data.setCreateTime(new Date());
        return ResponseMessage.ok(serverService.insert(data));
    }

    @RequestMapping(value = "/deleteServer/{id}", method = RequestMethod.DELETE)
    @AccessLogger("删除服务器")
    @Authorize(action = "D")
    public ResponseMessage delete(@PathVariable("id") Long id) {
        return ResponseMessage.ok(serverService.deleteServer(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @AccessLogger("编辑服务器")
    @Authorize(action = "U")
    public ResponseMessage update(@RequestBody Server data) {
        String message = data.validate();
        if (message != null) return ResponseMessage.error(message);
        return ResponseMessage.ok(serverService.update(data));
    }

    @RequestMapping(value = "/selectInfo/{id}", method = RequestMethod.GET)
    @AccessLogger("查询服务器详情")
    @Authorize(action = "R")
    public ResponseMessage serverInfo(@PathVariable("id") Long id) {
        return ResponseMessage.ok(serverService.queryServerInfo(id)).onlyData();
    }

    @RequestMapping(value = "/selectAll", method = RequestMethod.GET)
    @AccessLogger("分页查询服务器列表")
    @Authorize(action = "R")
    public ResponseMessage listServer(QueryParam param) {
        return ResponseMessage.ok(serverService.queryServer(param))
                .include(getPOType(), param.getIncludes())
                .exclude(getPOType(), param.getExcludes())
                .onlyData();
    }

    @RequestMapping(value = "/queryDevice/{id}", method = RequestMethod.GET)
    @AccessLogger("查询没设防的设备和已关联设备")
    @Authorize(action = "R")
    public ResponseMessage queryDevice(@PathVariable("id") Long id) {
        return ResponseMessage.ok(serverService.queryCamera(id));
    }

    @RequestMapping(value = "/addDevice", method = RequestMethod.POST)
    @AccessLogger("服务器关联设备")
    @Authorize(action = "C")
    public ResponseMessage addDevice(@RequestBody ServerDevice data) {
        if(data.getDeviceIdList() != null && serverDeviceService.createQuery().where(ServerDevice.Property.serverId,data.getServerId())
                .and(ServerDevice.Property.deviceId,data.getDeviceIdList()[0]).single() != null){
            return ResponseMessage.error("设备已关联");
        }
        return ResponseMessage.ok(serverService.addDevice(data));
    }



}
