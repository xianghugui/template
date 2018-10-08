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

    private int pageIndex;

    private int pageSize;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

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
}
