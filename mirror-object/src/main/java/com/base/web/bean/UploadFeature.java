package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

public class UploadFeature extends GenericPo<Long> {
    //人脸特征值
    private byte[] faceFeature;

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }

    public interface Property extends GenericPo.Property {
        String faceFeature = "faceFeature";
    }
}
