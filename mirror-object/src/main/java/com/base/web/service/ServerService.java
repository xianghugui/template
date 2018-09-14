package com.base.web.service;

import com.base.web.bean.Server;
import com.base.web.bean.ServerDevice;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;

public interface ServerService extends GenericService<Server, Long> {
    String addDevice(ServerDevice serverDevice);
    Server queryServerInfo(Long id);
    PagerResult<Server> queryServer(QueryParam param);
}
