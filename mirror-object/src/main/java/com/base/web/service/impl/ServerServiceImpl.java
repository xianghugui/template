package com.base.web.service.impl;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.DeleteParam;
import com.base.web.bean.common.InsertParam;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.ServerDeviceMapper;
import com.base.web.dao.ServerMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("serverService")
public class ServerServiceImpl extends AbstractServiceImpl<Server, Long> implements ServerService {

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private ServerDeviceMapper serverDeviceMapper;

    @Override
    protected GenericMapper<Server, Long> getMapper() {
        return serverMapper;
    }

    @Override
    public String addDevice(ServerDevice serverDevice) {
        //添加关联设备
        if (serverDevice.getDeviceIdList() != null && serverDevice.getDeviceIdList().length > 0) {
            for (Long deviceId : serverDevice.getDeviceIdList()) {
                serverDevice.setDeviceId(deviceId);
                serverDevice.setId(GenericPo.createUID());
                serverDeviceMapper.insert(InsertParam.build(serverDevice));
            }
            serverMapper.updateCameraStatus(1, serverDevice.getDeviceIdList());
        }
        //取消关联设备
        if (serverDevice.getCancelDeviceIdList() != null && serverDevice.getCancelDeviceIdList().length > 0) {
            for (Long deviceId : serverDevice.getCancelDeviceIdList()) {
                serverDevice.setDeviceId(deviceId);
                serverDeviceMapper.delete(DeleteParam.build()
                        .where(ServerDevice.Property.deviceId, serverDevice.getDeviceId())
                        .and(ServerDevice.Property.serverId, serverDevice.getServerId()));
            }
            serverMapper.updateCameraStatus(0, serverDevice.getDeviceIdList());
        }

        return "关联成功";
    }

    @Override
    public PagerResult<Server> queryServer(QueryParam param) {
        PagerResult<Server> pagerResult = new PagerResult<>();
        int total = serverMapper.queryServerTotal(param);
        pagerResult.setTotal(total);
        if (total == 0) {
            pagerResult.setData(new ArrayList<>());
        } else {
            //根据实际记录数量重新指定分页参数
            param.rePaging(total);
            pagerResult.setData(serverMapper.queryServer(param));
        }
        return pagerResult;
    }

    @Override
    public Server queryServerInfo(Long id) {
        Server server = serverMapper.queryServerInfo(id);
        return server;
    }

    @Override
    public List<Camera> queryCamera(Long id) {
        return serverMapper.queryCamera(id);
    }

    @Override
    public int deleteServer(Long id) {
        return serverMapper.deleteServer(id);
    }
}
