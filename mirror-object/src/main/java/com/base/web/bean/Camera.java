package com.base.web.bean;

import com.base.web.bean.po.GenericPo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Camera extends GenericPo<Long> {


    private String code; //设备编号
    private String name;
    private String ip;
    private Integer port;
    private Long organizationId;  //组织机构
    private String address;
    @JsonIgnore
    private Long associationCode; //摄像头-服务器：关联编号
    private Integer status; //0:未布防，1:已布防
    private Date createTime;
    private String note;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAssociationCode() {
        return associationCode;
    }

    public void setAssociationCode(Long associationCode) {
        this.associationCode = associationCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public interface Property extends GenericPo.Property {
        String CODE = "code";
        String NAME = "name";
        String PORT = "port";
        String IP = "ip";
        String ORGANIZATIONID = "organizationId";
        String ADDRESS = "address";
        String ASSOCIATIONCODE = "associationCode";
        String STATUS = "status";
        String CREATETIME = "createTime";
        String NOTE = "note";
    }
}
