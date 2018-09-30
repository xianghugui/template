package com.base.web.bean;

import java.util.Date;

/**
 * 人脸检索参数类
 */

public class UploadValue {
    //上传图片人脸特征值ID
    private Long uploadId;
    //组织ID
    private String organizationId;
    //设备ID
    private Long deviceId;
    //检索开始时间
    private String searchStart;
    //检索结束时间
    private String searchEnd;
    //最小相识度
    private Float minSimilarity;
    //最大相识度
    private Float maxSimilarity;

    public Long getUploadId() {
        return uploadId;
    }

    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getSearchStart() {
        return searchStart;
    }

    public void setSearchStart(String searchStart) {
        this.searchStart = searchStart;
    }

    public String getSearchEnd() {
        return searchEnd;
    }

    public void setSearchEnd(String searchEnd) {
        this.searchEnd = searchEnd;
    }

    public Float getMinSimilarity() {
        return minSimilarity;
    }

    public void setMinSimilarity(Float minSimilarity) {
        this.minSimilarity = minSimilarity/100;
    }

    public Float getMaxSimilarity() {
        return maxSimilarity;
    }

    public void setMaxSimilarity(Float maxSimilarity) {
        this.maxSimilarity = maxSimilarity/100;
    }
}
