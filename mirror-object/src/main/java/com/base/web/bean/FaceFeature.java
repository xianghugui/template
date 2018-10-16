package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class FaceFeature extends GenericPo<Long> {
    //人脸图片关联ID
    private Long resourceId;
    //人脸特征值
    private byte[] faceFeature;
    //创建时间
    private Date createTime;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public interface Property extends GenericPo.Property {
        String resourceId = "resourceId";
        String faceFeature = "faceFeature";
        String createTime = "createTime";
    }
}
