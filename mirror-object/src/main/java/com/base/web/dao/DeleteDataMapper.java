package com.base.web.dao;

import com.base.web.bean.AimsMessageDTO;
import com.base.web.bean.UploadValue;

import java.util.List;

public interface DeleteDataMapper {

    /**
     * 删除资源表t_resource数据
     * @param uploadValue
     */
    void deleteResource(UploadValue uploadValue);

    /**
     * 删除黑名单表t_association_blicklist数据
     * @param uploadValue
     * @return
     */
    void deleteAssociationBlackList(UploadValue uploadValue);

    /**
     * 删除人脸特征表t_face_feature数据
     * @param uploadValue
     * @return
     */
    void deleteFaceFeature(UploadValue uploadValue);

    /**
     * 删除人脸特征关联表t_face_image数据
     * @param uploadValue
     * @return
     */
    void deleteFaceImage(UploadValue uploadValue);


}
