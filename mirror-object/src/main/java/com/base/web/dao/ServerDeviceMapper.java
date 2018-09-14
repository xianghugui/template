package com.base.web.dao;


import com.base.web.bean.ServerDevice;

public interface ServerDeviceMapper extends GenericMapper<ServerDevice, Long> {

    int insertServerDevice(ServerDevice serverDevice);
}
