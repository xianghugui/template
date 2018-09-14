package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

public class ServerDevice extends GenericPo<Long> {
    //设备ID
    private Long deviceId;
    //服务器ID
    private Long serverId;

    //拓展字段设备ID数组
    private Long[] deviceIdList;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long[] getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(Long[] deviceIdList) {
        this.deviceIdList = deviceIdList;
    }

    public interface Property extends GenericPo.Property {
        String deviceId = "deviceId";
        String serverId = "serverId";
    }
}
