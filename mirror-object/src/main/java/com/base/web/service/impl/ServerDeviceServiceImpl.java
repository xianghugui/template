package com.base.web.service.impl;

import com.base.web.bean.ServerDevice;
import com.base.web.dao.GenericMapper;
import com.base.web.dao.ServerDeviceMapper;
import com.base.web.service.ServerDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("serverDeviceService")
public class ServerDeviceServiceImpl extends AbstractServiceImpl<ServerDevice, Long> implements ServerDeviceService {

    @Autowired
    private ServerDeviceMapper serverDeviceMapper;

    @Override
    protected GenericMapper<ServerDevice, Long> getMapper() {
        return serverDeviceMapper;
    }
}
