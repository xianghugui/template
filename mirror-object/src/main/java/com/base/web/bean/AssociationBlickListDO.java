package com.base.web.bean;

import com.base.web.bean.po.GenericPo;

import java.util.Date;

public class AssociationBlickListDO extends GenericPo<Long> {
    private Long faceImageId;
    private Long blackListId;
    //相似度
    private int similarity;
    //创建时间
    private Date createTime;

    public int getSimilarity() {
        return similarity;
    }

    public void setSimilarity(int similarity) {
        this.similarity = similarity;
    }

    public Long getFaceImageId() {
        return faceImageId;
    }

    public void setFaceImageId(Long faceImageId) {
        this.faceImageId = faceImageId;
    }

    public Long getBlackListId() {
        return blackListId;
    }

    public void setBlackListId(Long blackListId) {
        this.blackListId = blackListId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public interface Property extends GenericPo.Property{
        String faceImageId = "faceImageId";
        String blackListId = "blackListId";
        String similarity = "similarity";
        String createTime = "createTime";
    }

}
