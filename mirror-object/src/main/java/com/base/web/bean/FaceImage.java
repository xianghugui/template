package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class FaceImage extends GenericPo<Long> {
    //人脸图片关联ID
    private Long resourceId;
    //摄像头id
    private Long deviceId;
    //黑名单id
    private Long blacklistId;
    //创建时间
    private Date createTime;
    //黑名单对比相识度
    private int similarity;

    public Long getBlacklistId() {
        return blacklistId;
    }

    public void setBlacklistId(Long blacklistId) {
        this.blacklistId = blacklistId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public interface Property extends GenericPo.Property {
        String resourceId = "resourceId";
        String deviceId = "deviceId";
        String blacklistId = "blacklistId";
        String createTime = "createTime";
        String similarity = "similarity";
    }
}
