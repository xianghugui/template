package com.base.web.service;

import com.base.web.bean.FaceImage;

import java.util.List;
import java.util.Map;

public interface FaceImageService extends GenericService<FaceImage, Long>{
    List<Map> queryAllFaceImage(Long deviceId);
}
