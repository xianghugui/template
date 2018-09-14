package com.base.web.dao;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.common.QueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServerMapper extends GenericMapper<Server, Long> {

    Server queryServerInfo(Long id);
    List<Server> queryServer(QueryParam param);
    int queryServerTotal(QueryParam param);
    List<Camera> queryCamera(Long id);
    int deleteServer(Long id);
    void updateCameraStatus(@Param("status")int status,@Param("list")Long[] list);
}
