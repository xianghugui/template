package com.base.web.dao;

import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;

import java.util.List;
import java.util.Map;

public interface FaceImageMapper extends GenericMapper<FaceImage, Long>{
    List<Map> queryAllFaceImage(Long deviceId);
}
