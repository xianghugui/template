package com.base.web.bean;

import java.util.Date;
import java.util.List;

public class AimsMessageDTO {
    //faceImageId
    private Long id;
    private String deviceName;
    private Long resourceId;
    private Date createTime;
    private String imageUrl;
    private float similarity;
    //黑名单名称
    private String blackListName;
    //身份证号
    private String code;
    List<FaceFeature> list;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getBlackListName() {
        return blackListName;
    }

    public void setBlackListName(String blackListName) {
        this.blackListName = blackListName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FaceFeature> getList() {
        return list;
    }

    public void setList(List<FaceFeature> list) {
        this.list = list;
    }
}
