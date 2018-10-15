package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

public class FaceFeature extends GenericPo<Long> {
    //人脸图片关联ID
    private Long resourceId;
    //人脸特征值
    private byte[] faceFeature;

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

    public interface Property extends GenericPo.Property {
        String resourceId = "resourceId";
        String faceFeature = "faceFeature";
    }
}
