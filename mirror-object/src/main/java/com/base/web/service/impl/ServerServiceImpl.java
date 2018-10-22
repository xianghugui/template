package com.base.web.service.impl;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.ServerDeviceMapper;
import com.base.web.dao.ServerMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.CameraService;
import com.base.web.service.ServerService;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.HCNetSDK;
import com.base.web.util.NetDvrInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("serverService")
public class ServerServiceImpl extends AbstractServiceImpl<Server, Long> implements ServerService {

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private ServerDeviceMapper serverDeviceMapper;

    @Autowired
    private CameraService cameraService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected GenericMapper<Server, Long> getMapper() {
        return serverMapper;
    }

    /**
     * 关联设备和取消关联设备
     * @param serverDevice
     * @return
     */
    @Override
    @Transactional
    public String addDevice(ServerDevice serverDevice) {
        Map map = new HashMap();
        String str = "";
        //添加关联设备
        if (serverDevice.getDeviceIdList() != null && serverDevice.getDeviceIdList().length > 0) {
            Camera camera;
            Long[] deviceIds = serverDevice.getDeviceIdList();
            for (int i = 0, length = deviceIds.length;  i < length; i++) {
                camera = cameraService.selectByPk(deviceIds[i]);
                //设置报警回调函数
                Long userId = NetDvrInit.login(camera);
                if (userId == 0xFFFFFFFF || userId == 0xFFFFFFFFL) {
                    logger.error("IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                            + "password:"+ camera.getPassword() + "登陆失败，错误码：" + NetDvrInit.getLastError());
                    str += camera.getCode() + "：登陆失败，" + (HCNetSDK.ERROR_MAP.containsKey(NetDvrInit.getLastError()) ?
                            HCNetSDK.ERROR_MAP.get(NetDvrInit.getLastError()) : "错误码:" + NetDvrInit.getLastError() + "\n");
                    continue;
                } else {
                    camera.setUserId(userId);
                }
                Long alarmHandleId = NetDvrInit.setupAlarmChan(userId);
                if (alarmHandleId == 0xFFFFFFFF || alarmHandleId == 0xFFFFFFFFL) {
                    logger.error("IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                            + "password:"+ camera.getPassword() + "报警布防失败，错误码：" + NetDvrInit.getLastError());
                    str += camera.getCode() + "：布防失败，" + (HCNetSDK.ERROR_MAP.containsKey(NetDvrInit.getLastError()) ?
                            HCNetSDK.ERROR_MAP.get(NetDvrInit.getLastError()) : "错误码:" + NetDvrInit.getLastError() + "\n");
                    continue;
                } else {
                    camera.setAlarmHandleId(alarmHandleId);
                }
                FaceFeatureUtil.ENGINEMAPS.put(camera.getId(),new FaceFeatureUtil());
                camera.setStatus(1);
                cameraService.update(camera);
                serverDevice.setDeviceId(deviceIds[i]);
                serverDevice.setId(GenericPo.createUID());
                serverDeviceMapper.insert(InsertParam.build(serverDevice));
            }
        }
        //取消关联设备
        if (serverDevice.getCancelDeviceIdList() != null && serverDevice.getCancelDeviceIdList().length > 0) {
            Long[] deviceIds = serverDevice.getCancelDeviceIdList();
            Camera camera;
            for (int i = 0; i < deviceIds.length; i++) {
                camera = cameraService.selectByPk(deviceIds[i]);
                NetDvrInit.login(camera);
                if (!NetDvrInit.closeAlarmChan(camera.getAlarmHandleId())) {
                    logger.error("摄像头ID:" + deviceIds[i] + "，报警撤防失败，错误码：" + NetDvrInit.getLastError());
                    deviceIds[i] = -1L;
                    str += camera.getCode() + "：撤防失败，" + (HCNetSDK.ERROR_MAP.containsKey(NetDvrInit.getLastError()) ?
                            HCNetSDK.ERROR_MAP.get(NetDvrInit.getLastError()) : "错误码:" + NetDvrInit.getLastError() + "\n");
                    continue;
                }
                if (!NetDvrInit.logout(camera.getUserId())) {
                    logger.error("摄像头ID:" + deviceIds[i] + "，登出，错误码：" + NetDvrInit.getLastError());
                }
                FaceFeatureUtil.ENGINEMAPS.get(camera.getId()).clearFaceEngine();
                FaceFeatureUtil.ENGINEMAPS.remove(camera.getId());
            }
            serverDeviceMapper.batchDeleteServerDevice(serverDevice);
            map.put("status", 0);
            map.put("list", serverDevice.getCancelDeviceIdList());
            serverMapper.updateCameraStatus(map);
        }

        return str.isEmpty() ? "保存完毕" : str;
    }

    /**
     * 分页查询服务器列表
     * @param param
     * @return
     */
    @Override
    public PagerResult<Server> queryServer(QueryParam param) {
        PagerResult<Server> pagerResult = new PagerResult<>();
        int total = serverMapper.queryServerTotal(param);
        pagerResult.setTotal(total);
        if (total == 0) {
            pagerResult.setData(new ArrayList<>());
        } else {
            pagerResult.setData(serverMapper.queryServer(param));
        }
        return pagerResult;
    }

    /**
     * 查询服务器详情
     * @param id
     * @return
     */
    @Override
    public Server queryServerInfo(Long id) {
        Server server = serverMapper.queryServerInfo(id);
        return server;
    }

    /**
     * 查询该服务器已关联和没关联的设备
     * @param id
     * @return
     */
    @Override
    public List<Camera> queryCamera(Long id) {
        return serverMapper.queryCamera(id);
    }

    /**
     * 删除服务器
     * @param id
     * @return
     */
    @Override
    @Transactional
    public String deleteServer(Long id) {
        serverMapper.delete(DeleteParam.build().where(Server.Property.id, id));
        Long[] list = serverDeviceMapper.queryByServerId(id);
        if (list != null && list.length > 0) {
            Map map = new HashMap();
            serverDeviceMapper.delete(DeleteParam.build().where(ServerDevice.Property.serverId, id));
            map.put("status", 0);
            map.put("list", list);
            serverMapper.updateCameraStatus(map);
        }
        return "删除成功";
    }
}
