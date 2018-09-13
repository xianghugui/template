package com.base.web.service;

import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;

public interface ServerService extends GenericService<Server, Long> {
    String addDevice(ServerDevice serverDevice);
}
