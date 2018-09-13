package com.base.web.service.impl;

import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.InsertParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.ServerDeviceMapper;
import com.base.web.dao.ServerMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public String addDevice(ServerDevice serverDevice){
        ServerDevice insertData = new ServerDevice();
        insertData.setServerId(serverDevice.getServerId());
        insertData.setAssociationCode(GenericPo.createUID());
        for(Long item:serverDevice.getDeviceIdList()){
            insertData.setDeviceId(item);
            serverDeviceMapper.insert(InsertParam.build(insertData));
        }
        return "关联成功";
    }
}
