package com.base.web.bean;

import com.base.web.bean.po.GenericPo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Server extends GenericPo<Long> {
    //服务器名称
    private String name;
    //摄像头关联码
    @JsonIgnore
    private String associationCode;
    //创建时间
    private Date createTime;
    //备注
    private String note;
    //服务器地址
    private String serverIp;
    //服务器端口
    private Integer serverPort;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssociationCode() {
        return associationCode;
    }

    public void setAssociationCode(String associationCode) {
        this.associationCode = associationCode;
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

    public interface Property extends GenericPo.Property {
        String name = "name";
        String associationCode = "associationCode";
        String createTime = "createTime";
        String note = "note";
        String serverIp = "serverIp";
        String serverPort = "serverPort";
    }
}
