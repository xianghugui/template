package com.base.web.bean;


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

    //黑名单人物ID
    private Long blackListId;

    private int pageIndex;

    private int pageSize;

    /**
     * 复制对象
     * @param uploadValue
     * @return
     */
    public static UploadValue copyUploadValue(UploadValue uploadValue) {
        return new UploadValue(uploadValue.getUploadId(), uploadValue.getOrganizationId(), uploadValue.getDeviceId(),
                uploadValue.getSearchStart(), uploadValue.getSearchEnd(), uploadValue.getMinSimilarity(),
                uploadValue.getPageIndex(), uploadValue.getPageSize());
    }

    public UploadValue(Long uploadId, String organizationId, Long deviceId, String searchStart, String searchEnd, Float minSimilarity,
                       int pageIndex, int pageSize) {
        this.uploadId = uploadId;
        this.organizationId = organizationId;
        this.deviceId = deviceId;
        this.searchStart = searchStart;
        this.searchEnd = searchEnd;
        this.minSimilarity = minSimilarity;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public UploadValue(){
        super();
    }

    public Long getBlackListId() {
        return blackListId;
    }

    public void setBlackListId(Long blackListId) {
        this.blackListId = blackListId;
    }

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
