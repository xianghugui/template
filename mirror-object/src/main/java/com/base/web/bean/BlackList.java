package com.base.web.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class BlackList extends GenericPo<Long> {
    //身份证号码
    private String code;
    //名字
    private String name;
    //资源ID
    @JSONField(serialize = false)
    private Long resourceId;

    //人脸特征值
    @JSONField(serialize = false)
    private byte[] faceFeature;

    private Date createTime;

    private String imageUrl;

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public interface Property extends GenericPo.Property{
        String code = "code";
        String name = "name";
        String resourceId = "resourceId";
        String faceFeature = "faceFeature";
        String createTime = "createTime";
    }
}
