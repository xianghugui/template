package com.base.web.dao;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.common.QueryParam;

import java.util.List;
import java.util.Map;

public interface ServerMapper extends GenericMapper<Server, Long> {

    Server queryServerInfo(Long id);
    List<Server> queryServer(QueryParam param);
    int queryServerTotal(QueryParam param);
    List<Camera> queryCamera(Long id);
    void updateCameraStatus(Map map);
}
