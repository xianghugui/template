package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class UploadFeature extends GenericPo<Long> {
    //人脸特征值
    private byte[] faceFeature;

    private Date createTime;

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
        String faceFeature = "faceFeature";
        String createTime = "createTime";
    }
}
