package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class Camera extends GenericPo<Long> {

    private String code; //设备编号
    private String name;
    private String ip;
    private Integer port;
    private Integer aisle; //通道
    private Long organizationId;  //组织机构
    private String address;
    private Integer status; //0:未布防，1:已布防
    private String note;
    private String account;
    private String password;
    private Date createTime;

    /**
     * 验证参数是否为空
     * @return
     */
    public String validate() {
        if ("".equals(ip)) return "请输入IP";
        if (port == null) return "请输入端口";
        if ("".equals(account)) return "请输入账号";
        if ("".equals(password)) return "请输入密码";
        return null;
    }

    public Integer getAisle() {
        return aisle;
    }

    public void setAisle(Integer aisle) {
        this.aisle = aisle;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
        String STATUS = "status";
        String CREATETIME = "createTime";
        String NOTE = "note";
        String AISLE = "aisle";
        String ACCOUNT = "account";
        String PASSWORD = "password";
    }
}
