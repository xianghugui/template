package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class Camera extends GenericPo<Long> {

    private String code; //设备编号
    private String name;
    private String ip;
    private short port; //服务端口
    private short httpPort; //http端口
    private Integer aisle; //通道
    private Long organizationId;  //组织机构
    private String address;
    private Integer status; //0:未布防，1:已布防
    private String note;
    private String account;
    private String password;
    private Date createTime;
    private Long alarmHandleId; //设置报警回调函数返回的id，用于撤防
    private Long userId; //设备登陆时返回的ID，用于抓拍时获取u_id

    /**
     * 验证参数是否为空
     * @return
     */
    public String validate() {
        if ("".equals(ip)) return "请输入IP";
        if ("".equals(account)) return "请输入账号";
        if ("".equals(password)) return "请输入密码";
        return null;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public short getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(short httpPort) {
        this.httpPort = httpPort;
    }

    public Long getAlarmHandleId() {
        return alarmHandleId;
    }

    public void setAlarmHandleId(Long alarmHandleId) {
        this.alarmHandleId = alarmHandleId;
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

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
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
        String HTTPPORT = "httpPort";
        String IP = "ip";
        String ORGANIZATIONID = "organizationId";
        String ADDRESS = "address";
        String STATUS = "status";
        String CREATETIME = "createTime";
        String NOTE = "note";
        String AISLE = "aisle";
        String ACCOUNT = "account";
        String PASSWORD = "password";
        String ALARMHANDLEID = "alarmHandleId";
        String USERID = "userId";
    }
}
