package com.base.web.dao;


import com.base.web.bean.ServerDevice;

public interface ServerDeviceMapper extends GenericMapper<ServerDevice, Long> {
    /**
     * 通过服务器id查询设备id
     * @param id
     * @return
     */
    Long[] queryByServerId(Long id);

    /**
     * 取消关联设备
     * @param serverDevice
     * @return
     */
    int batchDeleteServerDevice(ServerDevice serverDevice);
}
