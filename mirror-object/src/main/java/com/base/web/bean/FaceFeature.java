package com.base.web.bean;

import com.base.web.bean.po.GenericPo;
import oracle.sql.BLOB;

public class FaceFeature extends GenericPo<Long> {
    //人脸图片关联ID
    private Long refId;
    //人脸特征值
    private BLOB faceFeature;

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public BLOB getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(BLOB faceFeature) {
        this.faceFeature = faceFeature;
    }

    public interface Property extends GenericPo.Property {
        String refId = "refId";
        String faceFeature = "faceFeature";
    }
}
