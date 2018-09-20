package com.base.web.bean;

import com.base.web.bean.po.GenericPo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Server extends GenericPo<Long> {
    //服务器名称
    private String name;
    //创建时间
    private Date createTime;
    //备注
    private String note;
    //服务器地址
    private String serverIp;
    //服务器端口
    private Integer serverPort;

    //拓展字段 关联设备列表
    private String deviceList;

    /**
     * 验证参数是否为空
     * @return
     */
    public String validate() {
        if ("".equals(serverIp)) return "请输入IP";
        if (serverPort == null) return "请输入端口";
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(String deviceList) {
        this.deviceList = deviceList;
    }

    public interface Property extends GenericPo.Property {
        String name = "name";
        String createTime = "createTime";
        String note = "note";
        String serverIp = "serverIp";
        String serverPort = "serverPort";
        String deviceList = "deviceList";
    }
}
