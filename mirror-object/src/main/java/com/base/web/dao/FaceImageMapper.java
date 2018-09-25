package com.base.web.dao;

import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;

import java.util.List;
import java.util.Map;

public interface FaceImageMapper extends GenericMapper<FaceImage, Long>{

    /**
     * 根据摄像头ID查询全部监测图片
     * @param deviceId
     * @return
     */
    List<Map> queryAllFaceImage(Long deviceId);
}
