package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

public class FaceFeature extends GenericPo<Long> {
    //人脸图片关联ID
    private Long refId;
    //人脸特征值
    private byte[] faceFeature;

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public interface Property extends GenericPo.Property {
        String refId = "refId";
        String faceFeature = "faceFeature";
    }
}
