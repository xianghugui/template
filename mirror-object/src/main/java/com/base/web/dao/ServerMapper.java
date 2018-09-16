package com.base.web.dao;

import com.base.web.bean.Camera;
import com.base.web.bean.Server;
import com.base.web.bean.common.QueryParam;

import java.util.List;
import java.util.Map;

public interface ServerMapper extends GenericMapper<Server, Long> {

    /**
     * 查询服务器详情
     * @param id
     * @return
     */
    Server queryServerInfo(Long id);

    /**
     * 分页查询服务器列表
     * @param param
     * @return
     */
    List<Server> queryServer(QueryParam param);

    /**
     * 查询服务器条数
     * @param param
     * @return
     */
    int queryServerTotal(QueryParam param);

    /**
     * 查询服务器已设防和没设防的设备
     * @param id
     * @return
     */
    List<Camera> queryCamera(Long id);

    /**
     * 批量更改设备状态
     * @param map{
     *   list:设备id数组
     *   status: 0:未激活状态,1:激活状态
     * }
     */
    void updateCameraStatus(Map map);
}
