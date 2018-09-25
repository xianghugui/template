package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

public class FaceFeature extends GenericPo<Long> {
    //人脸图片关联ID
    private Long faceImageId;
    //人脸特征值
    private byte[] faceFeature;

    public Long getFaceImageId() {
        return faceImageId;
    }

    public void setFaceImageId(Long faceImageId) {
        this.faceImageId = faceImageId;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public interface Property extends GenericPo.Property {
        String faceImageId = "faceImageId";
        String faceFeature = "faceFeature";
    }
}
